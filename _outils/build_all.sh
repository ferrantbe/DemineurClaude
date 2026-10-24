#!/bin/sh
# Regenere l'enonce et le corrige de bout en bout (docx pagines et
# typographies, PDF si LibreOffice est la), puis le projet etudiant.
#     sh _outils/build_all.sh
set -e
cd "$(dirname "$0")/.."
python3 _documents/schemas/schemas.py
node _outils/build_demineur.js
node _outils/build_corrige.js
pages() { python3 -c "from pypdf import PdfReader; print(len(PdfReader('$1').pages))"; }
if command -v soffice >/dev/null; then
  (cd _documents && soffice --headless --convert-to pdf Demineur_enonce.docx Demineur_corrige.docx >/dev/null 2>&1)
  E=$(pages _documents/Demineur_enonce.pdf); C=$(pages _documents/Demineur_corrige.pdf)
else
  E=${1:-5}; C=${2:-6}
fi
python3 _outils/ajouter_pagination.py _documents/Demineur_enonce.docx "Démineur" $((E-1))
python3 _outils/ajouter_pagination.py _documents/Demineur_corrige.docx "Démineur - corrigé" $((C-1))
python3 _outils/typographie.py _documents/Demineur_enonce.docx _documents/Demineur_corrige.docx
rm -f _documents/*.avant
if command -v soffice >/dev/null; then
  (cd _documents && soffice --headless --convert-to pdf Demineur_enonce.docx Demineur_corrige.docx >/dev/null 2>&1)
fi
python3 _outils/depot_demineur.py
