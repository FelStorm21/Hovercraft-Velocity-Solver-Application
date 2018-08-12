# Hovercraft Velocity Solver
This Java application runs a numerical solver of a Riccati differential equation to solve for the velocity of a hovercraft as a function of time. Computation is facilitated by the Apache Math library; graphing is facilitated by the JFreeChart library (which itself uses JCommon); testing is done using TestNG. Users interact through a basic GUI written in Swing.

This project was created as a supplement to the research paper "Thrust Analysis of a non-Integrated Hovercraft" by Benjamin D. Zalla. Snippets of code from this project are included in the paper, and a link to this repository is given in it as well.

The executable and jar files are given under the "Releases" section of this page. Both the .jar and Windows .exe files are exactly the same application, with the .exe version being created using launch4j software to wrap the .jar into an .exe. The .exe comes with a basic desktop thumbnail for ease of access.

The Maven build file is given in the source distribution dist.zip, if the user desires to build the application themselves. Note that if the user does want to build the project themselves, and specifically import the project into Eclipse, that the apparent Studio One file in the distribution is actually an Eclipse .project file. My computer has Presonus Studio One downloaded, and both applications use the same file extension. The project should still work in Eclipse. Also note that the "original-hcSolverApp-1.0.jar" in the "target" directory of the source distribution is the unshaded version of the "hcSolverApp-1.0.jar". The shaded jar is useful as it contains the <MainClass> descriptor and so can be run from cmd with a simple "java -jar pathToJar.jar" command.
 
