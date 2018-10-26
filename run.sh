mkvirtualenv sphinx-markdown
pip install --upgrade pip
pip install -r requirements.txt
make html
open _build/html/index.html
