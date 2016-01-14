Jeffrey Thompson
jeffrelt
12987953

I created a bash script to automate testing. This file is named run_tests and is 
ran from the root of the project with the tests_public folder placed there also. 

The script does the following:

1) compile all the .java files and put them in bin/crux folder. 
  
2) Look in all directors matching tests* and run the program on all the .crx files
   found within. 

3) The output is piped to diff which is compared to the matching .out
   file. I wanted it to end on a error found so that is what it does.

4) if all the tests pass the script says so and exits.

If interested the script file is included, but here are the basics as ran from 
the project root directory to compile and run the program on a crux file:

javac src/crux/*java -d bin
java -cp ./bin crux.Compiler {path to crux src file}
