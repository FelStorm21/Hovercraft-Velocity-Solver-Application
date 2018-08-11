# Hovercraft Velocity Solver
This Java application runs a numerical solver of a Riccati differential equation to solve for the velocity of a hovercraft as a function of time. Computation is facilitated by the Apache Math library; graphing is facilitated by the JFreeChart library (which itself uses JCommon); testing is done using TestNG. Users interact through a basic GUI written in Swing.

This project was created as a supplement to the research paper "Thrust Analysis of a non-Integrated Hovercraft" by Benjamin D. Zalla. Snippets of code from this project are included in the paper, and a link to this repository is given in it as well.

The entire Eclipse project is included for import. Note that if you have Presonus Studio One installed on your computer, the Eclipse .project file will have the same file extension as a Studio One .project. This shouldn't affect the functionality, as long as you do not rename the file.

The executable and jar files can be found within the "target" directory. Both the .jar and .exe files are exactly the same application, with the .exe version being created using launch4j software to wrap the .jar into an .exe. The .exe comes with a basic desktop thumbnail for ease of access.
