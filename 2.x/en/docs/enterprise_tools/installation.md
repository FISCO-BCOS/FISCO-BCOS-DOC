# Download & install

## Environment dependency

Dependency of FISCO BCOS generator:

| Dependent software | Supported version |
| :----------------: | :---------------: |
|       python       |    2.7+ or 3.6+   |
|       openssl      |      1.0.2k+      |
|        curl        |  default version  |
|         nc         |  default version  |

## Download & install

Download

```bash
$ git clone https://github.com/FISCO-BCOS/generator.git

# If you have network issue for exec the command above, please try:
$ git clone https://gitee.com/FISCO-BCOS/generator.git
```

Install

```bash
$ cd generator
$ bash ./scripts/install.sh
```

Check if it is installed successfully.

```bash
$ ./generator -h
# if succeed, output usage: generator xxx
```

## Pull node binaries

Pull the latest fisco-bcos binary files to meta.

```bash
$ ./generator --download_fisco ./meta
```

Check binaries version.

```bash
$ ./meta/fisco-bcos -v
# if succeed, output FISCO-BCOS Version : x.x.x-x
```

**PS**：[Source code compilation](../manual/get_executable.md) node binaries user need only to put the compiled binaries to `meta` directory.
