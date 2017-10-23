# ner

NER használata parancsorból
Train
java -Xmx10G -jar ner.jar -mode train
-t #train file path
-e #train file encoding
-s #ascii code of the train file column separator (9=tab, 32=space)
-w #word (token) column index
-l #label column index
-p #parameter file path
-m #trained (output) model file path
Example
java -Xmx10G -jar ner.jar -mode train -t ./data/hvg.conll.train -e utf-8 -s 32 -w 0 -l 1 -p ./data/param/1.param -m ./out.model
Predicate
java -Xmx10G -jar ner.jar -mode predicate
-m #trained model file path
-t #test file
-e #test file encoding
-s #ascii code of the test file column separator (9=tab, 32=space)
-w #word (token) column index
-l #label column index
[-i #instance log file]
-p #predicated (output) file
Example
java -Xmx10G -jar ner.jar -mode predicate -m ./out.model -t ./data/hvg.conll.test -e utf-8 -s 32 -w 0 -l 1 -i instance.txt -p ./predicated.txt
Evaluate
java -Xmx10G -jar ner.jar -mode eval
-p #predicated (output) file
-e #predicated file encoding
-s #ascii code of the predicated file column separator (9=tab, 32=space)
-w #word (token) column index
-g #gold label column index
-l #predicated label column index
-r #result file (f1-measure)
Example
nice -19 java -Xmx10G -jar ner.jar -mode eval -p ./predicated.txt -e utf-8 -s 32 -w 0 -g 1 -l 2 -r ./result.txt
