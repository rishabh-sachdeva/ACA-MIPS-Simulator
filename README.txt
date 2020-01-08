1. To Run the program with provided executable jar:
	Prerequisite : java 1.8
	Go to the directory(Sachdeva_ACA_project) and execute the simulate.sh file with arguments.
	./simulate.sh inst.txt data.txt reg.txt config.txt result.txt 

## please note order of arguments is important.
##Provide relative path or absolute path of files in arguments of simulate.sh

2. To create new executable jar:
	Prerequisites : 
	a. maven
	b. java 1.8
	Steps:
		1. Go to the directory(Sachdeva_ACA_project):
		   run "mvn clean install"
		   This command should create the new executable jar in target folder.
		2. Now, run "./simulate.sh inst.txt data.txt reg.txt config.txt result.txt"