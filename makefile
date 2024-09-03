testFile: 
	mvn	exec:java -Dexec.args="./src/test/resources/crux/stages/codegen/test09.crx"

maven:
	mvn clean
	mvn compile

run: a.o runtime.o
	gcc a.o runtime.o -o run

a.o: a.s
	gcc -c -g a.s -o a.o

runtime.o: src/runtime/runtime.c
	gcc -c -g src/runtime/runtime.c -o runtime.o

clean: 
	rm -f a.o runtime.o run

