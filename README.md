# ner

NER használata parancsorból<br />

Train<br />
java -Xmx10G -jar ner.jar -mode train<br />
-t #train file path<br />
-e #train file encoding<br />
-s #ascii code of the train file column separator (9=tab, 32=space)<br />
-w #word (token) column index<br />
-l #label column index<br />
-p #parameter file path<br />
-m #trained (output) model file path<br />

Example<br />
java -Xmx10G -jar ner.jar -mode train -t ./data/hvg.conll.train -e utf-8 -s 32 -w 0 -l 1 -p ./data/param/1.param -m ./out.model<br />

Predicate<br />
java -Xmx10G -jar ner.jar -mode predicate<br />
-m #trained model file path<br />
-t #test file<br />
-e #test file encoding<br />
-s #ascii code of the test file column separator (9=tab, 32=space)<br />
-w #word (token) column index<br />
-l #label column index<br />
[-i #instance log file]<br />
-p #predicated (output) file<br />

Example<br />
java -Xmx10G -jar ner.jar -mode predicate -m ./out.model -t ./data/hvg.conll.test -e utf-8 -s 32 -w 0 -l 1 -i instance.txt -p ./predicated.txt<br />

Evaluate<br />
java -Xmx10G -jar ner.jar -mode eval<br />
-p #predicated (output) file<br />
-e #predicated file encoding<br />
-s #ascii code of the predicated file column separator (9=tab, 32=space)<br />
-w #word (token) column index<br />
-g #gold label column index<br />
-l #predicated label column index<br />
-r #result file (f1-measure)<br />

Example<br />
nice -19 java -Xmx10G -jar ner.jar -mode eval -p ./predicated.txt -e utf-8 -s 32 -w 0 -g 1 -l 2 -r ./result.txt<br />
