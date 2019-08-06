#!/bin/bash


LOG_ERROR() {
    content=${1}
    echo -e "\033[31m"${content}"\033[0m"
}

function check_PR_limit()
{
    local files=$(git diff --stat HEAD^ | grep docs | wc -l)
    if [ $(($files%2)) == 0 ] ; then
        echo "Modified files is including cn an en."
    else
        LOG_ERROR "modify ${files} files, limit is ${file_limit}"
        exit 1
    fi
}

check_PR_limit