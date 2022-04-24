/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Yehtzee_game_project.codes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Dell
 */
public class NewClass {

    public static void main(String[] args) throws IOException {
        String currentPath = new java.io.File(".").getCanonicalPath();
        System.out.println(currentPath);
        String s = currentPath.substring(0, currentPath.indexOf("Yehtzee")+7);
        System.out.println(currentPath.indexOf("Yehtzee")+7);
        System.out.println(s);
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" + currentDir);
        Path currentDir2 = Paths.get("."); // currentDir = "."
        Path fullPath = currentDir2.toAbsolutePath(); // fullPath = "/Users/guest/workspace"
        System.out.println(currentDir2);
        System.out.println("///////////////////");
        System.out.println(fullPath);
    }
}
