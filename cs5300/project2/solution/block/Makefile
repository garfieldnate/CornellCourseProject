hadoop = hadoop

bases = Node Edge NodeOrEdge BlockedMapper BlockedReducer FinalMapper FinalReducer BlockedPageRank 
#bases = Node Edge NodeOrEdge BlockedMapper GaussReducer FinalMapper FinalReducer BlockedPageRank
classDir = classes
sourceDir = src
HADOOP_PREFIX=/opt/
javaFiles = src/*.java
classFiles = classes/*.class

CLASS_PATH=$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/common/*:$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/yarn/lib/*:$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/mapreduce/lib/*:$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/mapreduce/*:./
j = -classpath $(CLASS_PATH) #Xlint:deprecation


default : $(javaFiles)
	mkdir $(classDir); javac $j -d $(classDir) $(javaFiles); jar cvf BlockedPageRank.jar $(classDir);echo $(classFiles); $(hadoop) jar BlockedPageRank.jar BlockedPageRank ../input ../output #> log.txt 2>&1

clean : 
	rm -r $(classDir); rm -r ../output/*; rm BlockedPageRank.jar; rm log.txt



