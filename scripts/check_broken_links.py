#!/usr/bin/env python3
"""检查文档改动中引入的相对链接失效问题。

只检查本次改动涉及的文档文件，避免历史上已存在的失效链接阻塞 CI：

* Markdown 行内链接与图片：``[text](path)``、``![alt](path)``
* reStructuredText 的 ``.. image::`` / ``.. include::`` 指令目标
* reStructuredText 的 ``:doc:`` 角色
* reStructuredText ``toctree`` 中的文档条目

目标路径按文档所在目录解析。Sphinx 里 ``*.html`` 链接对应的源文件
可能是同名 ``*.md`` 或 ``*.rst``；toctree 条目是省略扩展名的文档名，
两种源文件都会尝试匹配。

用法::

    python3 scripts/check_broken_links.py                 # 对比 HEAD^
    python3 scripts/check_broken_links.py --base <sha>    # 对比指定提交
    python3 scripts/check_broken_links.py a.md b.rst      # 指定文件
"""

import argparse
import os
import re
import subprocess
import sys

DOC_EXTENSIONS = (".md", ".rst")

MD_LINK = re.compile(r"\]\((<[^>]+>|[^)\s]+)")
# `文本 <目标>`_ 形式的 RST 超链接（Markdown 文档里也常见）
RST_LINK = re.compile(r"`[^`<]*<([^>]+)>`_")
RST_DIRECTIVE = re.compile(r"^\s*\.\.\s+(?:image|include)::\s+(.+?)\s*$")
RST_DOC_ROLE = re.compile(r":doc:`([^`]+)`")
TOCTREE = re.compile(r"^\s*\.\.\s+toctree::\s*$")
FENCE = re.compile(r"^(?:```|~~~)")

# 视为外链或页内锚点，无需校验
EXTERNAL_PREFIXES = ("#", "/", "http://", "https://", "mailto:", "ftp:")
# _static 目录通过 html_extra_path 复制到输出根目录，源码位置与引用位置不同，跳过
SKIP_PREFIXES = ("_static/",)


def git_changed_files(base):
    try:
        out = subprocess.check_output(
            ["git", "diff", "--name-only", "--diff-filter=ACMR", base, "HEAD"],
            universal_newlines=True,
        )
    except (subprocess.CalledProcessError, OSError) as exc:
        sys.exit("failed to read changed files from git: %s" % exc)
    return [line.strip() for line in out.splitlines() if line.strip()]


def strip_code_blocks(text):
    """把代码块内容清空，避免把示例代码里的链接当成真链接。

    ```eval_rst 块里的内容是会被渲染的 reStructuredText，需要保留。
    """
    lines = []
    in_fence = False
    in_eval_rst = False
    for line in text.splitlines():
        if FENCE.match(line.lstrip()):
            if in_eval_rst:
                in_eval_rst = False
                lines.append("")
                continue
            if not in_fence and "eval_rst" in line:
                in_eval_rst = True
                lines.append("")
                continue
            in_fence = not in_fence
            lines.append("")
            continue
        lines.append("" if in_fence else line)
    return "\n".join(lines)


def is_external(target):
    target = target.strip()
    if not target or "://" in target:
        return True
    return target.startswith(EXTERNAL_PREFIXES)


def clean_target(target):
    target = target.strip()
    if target.startswith("<") and target.endswith(">"):
        target = target[1:-1]
    return target.split("#", 1)[0].split("?", 1)[0].strip()


def source_candidates(path):
    """把 Sphinx 链接目标映射为实际存在的源文件路径。"""
    paths = [path]
    root, ext = os.path.splitext(path)
    if ext in (".html", ".htm"):
        paths.extend([root + ".md", root + ".rst"])
    elif ext == "":
        paths.extend([path + ".md", path + ".rst"])
    return paths


def toctree_entries(lines):
    """产出 toctree 指令下的 (行号, 文档名)。"""
    in_block = False
    indent = None
    for line_no, line in enumerate(lines, 1):
        if TOCTREE.match(line):
            in_block, indent = True, None
            continue
        if not in_block:
            continue
        if not line.strip():
            continue
        current = len(line) - len(line.lstrip())
        if indent is None:
            if line.strip().startswith(":"):
                continue  # toctree 选项，如 :maxdepth:
            indent = current
        elif current < indent:
            in_block = False
            continue
        entry = line.strip()
        if entry.startswith(":") or entry.startswith(".."):
            continue
        matched = re.search(r"<([^>]+)>$", entry)
        if matched:
            entry = matched.group(1)  # "标题 <路径>" 写法
        yield line_no, entry


def check_file(path):
    """返回该文件失效的相对链接列表 [(行号, 目标, 类型)]。"""
    if not os.path.isfile(path):
        return []
    with open(path, "r", encoding="utf-8", errors="replace") as handle:
        lines = strip_code_blocks(handle.read()).splitlines()

    base_dir = os.path.dirname(os.path.abspath(path))
    problems = []

    def check(target, line_no, kind):
        if is_external(target):
            return
        rel = clean_target(target)
        if not rel or rel.startswith(SKIP_PREFIXES):
            return
        full = os.path.normpath(os.path.join(base_dir, rel))
        if not any(os.path.exists(p) for p in source_candidates(full)):
            problems.append((line_no, target, kind))

    for line_no, line in enumerate(lines, 1):
        for match in MD_LINK.finditer(line):
            check(match.group(1), line_no, "markdown link")
        for match in RST_LINK.finditer(line):
            check(match.group(1), line_no, "rst link")
        match = RST_DIRECTIVE.match(line)
        if match:
            check(match.group(1), line_no, "rst directive")
        for match in RST_DOC_ROLE.finditer(line):
            check(match.group(1), line_no, "doc role")

    for line_no, entry in toctree_entries(lines):
        check(entry, line_no, "toctree")

    return problems


def main():
    parser = argparse.ArgumentParser(description="check relative links in changed docs")
    parser.add_argument("--base", default=os.environ.get("BASE_SHA", "HEAD^"),
                        help="对比的基线提交，默认 HEAD^ 或环境变量 BASE_SHA")
    parser.add_argument("files", nargs="*", help="显式指定待检查文件")
    args = parser.parse_args()

    files = args.files or git_changed_files(args.base)
    targets = [f for f in files if f.endswith(DOC_EXTENSIONS) and os.path.isfile(f)]
    if not targets:
        print("no document changes to check")
        return 0

    total = 0
    for path in targets:
        for line_no, target, kind in check_file(path):
            total += 1
            print("%s:%s: broken relative link '%s' (%s)" % (path, line_no, target, kind))

    if total:
        print("\n%d broken relative link(s) found" % total)
        return 1
    print("checked %d changed document(s), no broken relative links" % len(targets))
    return 0


if __name__ == "__main__":
    sys.exit(main())
