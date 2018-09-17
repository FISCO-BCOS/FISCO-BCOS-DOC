set -e
rm _build -rf
sphinx-build -b html -d _build/doctrees  . _build/html