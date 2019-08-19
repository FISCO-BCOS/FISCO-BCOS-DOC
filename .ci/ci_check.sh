#!/bin/bash


LOG_ERROR() {
    content=${1}
    echo -e "\033[31m"${content}"\033[0m"
}

function check_PR_limit()
{
    local files=$(git diff --stat HEAD^ | grep docs | wc -l)
    local en_files=$(git diff --stat HEAD^ | grep "en/docs" | wc -l)
    if [ $(($files % 2)) == 0 ]; then
        echo "Modified files have both cn and en."
    elif [ "${files}" == "${en_files}" ]; then
        echo "Modified files are all en."
    else
        LOG_ERROR "Modified files should include cn and en"
        git diff --stat HEAD^ | grep docs
        exit 1
    fi
}

check_PR_limit
