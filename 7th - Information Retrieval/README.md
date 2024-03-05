VSM implementation and comparison with ColBERT
Requires Cystic Fibrosis collection [https://people.ischool.berkeley.edu/%7Ehearst/irbook/cfc.html] saved in ./docs

Availabe Factors (Recommened Combinations):
tfc nfx
txc nfx
nfc nfx

First two are default from paper
Third was recommended for short queries
Fourth was extrapolated from findings

usaga example :

- python source/main.py n f c n f x      
- python source/main.py t f c n f x   
- python source/main.py t x c n f x     

Colbert File is an ipynb, and in order to be executed and work properly, it must be uploaded and executed through the Google Colaboratory environment for the Colbert libraries to work properly
