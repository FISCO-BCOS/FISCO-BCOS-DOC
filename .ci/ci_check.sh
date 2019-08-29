#!/bin/bash


LOG_ERROR() {
    content=${1}
    echo -e "\033[31m"${content}"\033[0m"
}

function check_PR()
{
    local files=$(git diff --stat=128 HEAD^ HEAD | grep docs | wc -l)
    local en_files=$(git diff --stat=128 HEAD^ HEAD | grep "en/docs" | wc -l)
    if [ $(($files % 2)) == 0 ]; then
        echo "Modified files have both cn and en."
    elif [ "${files}" == "${en_files}" ]; then
        echo "Modified files are all en."
    else
        LOG_ERROR "Modified files should include cn and en"
        git diff --stat HEAD^ HEAD 
        git show HEAD^ --stat --format=oneline
        git show HEAD --stat --format=oneline
        exit 1
    fi
    local commits=$(git rev-list --count HEAD^..HEAD)
    local unique_commit=$(git log --format=%s HEAD^..HEAD | sort -u | wc -l)
    if [ ${unique_commit} -ne ${commits} ];then
        LOG_ERROR "${commits} != ${unique_commit}, please make commit message unique!"
        exit 1
    fi
}

check_PR
