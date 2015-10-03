hadoop = hadoop

bases = Node NodeOrDouble SimpleMapper SimpleReducer SimplePageRank ResidualCounter
classDir = classes
sourceDir = src
HADOOP_PREFIX=/opt/
javaFiles = src/*.java
classFiles = classes/*.class

CLASS_PATH=$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/common/*:$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/yarn/lib/*:$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/mapreduce/lib/*:$(HADOOP_PREFIX)hadoop-2.6.0/share/hadoop/mapreduce/*:./
j = -classpath $(CLASS_PATH) #Xlint:deprecation


default : $(javaFiles)
	mkdir $(classDir); javac $j -d $(classDir) $(javaFiles); jar cvf SimplePageRank.jar $(classDir);echo $(classFiles); $(hadoop) jar SimplePageRank.jar SimplePageRank ../input ../output

clean : 
	rm -r $(classDir); rm -r ../output/stage*; rm SimplePageRank.jar;



