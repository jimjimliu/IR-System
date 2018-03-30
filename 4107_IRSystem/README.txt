2018-02-11: Junhan Liu

All java files have been complied using both Eclipse and Terminal.

In ir/vsr/Document.java, the stopwords's path was 'ir/utilities/stopwords.txt', and it was complied by Terminal for running in command line;

The stopwords path now is 'src/ir/utilities/stopwords.txt', and it was complied by Eclipse( all complied files are in 'bin/' ) for running in eclipse.

Done so is for running the program in both Eclipse and Terminal without encountering a illegal path error. 
-if running in command line, you dont need to recomplie it. Just locate the src folder and run the program.
-if running in Eclipse, it will complie the entire project everytime you run it, and the path should be an absolute path, that's why the path in the code now is set to 'src/ir/utilities/stopwords.txt'.

If (unlikely) you get a illegal path error, change the path of stopwords.txt in Document.java to your own local path of your stopwords text file. Recomplie ir/vsr/InvertedIndex.java, and SystemStart.java. 