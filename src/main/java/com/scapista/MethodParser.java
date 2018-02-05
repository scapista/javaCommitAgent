package com.scapista;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ParseException;


import java.io.File;
import java.io.IOException;

public class MethodParser {
    private String srcFileName = null;
    private String dirName = null;

    MethodParser(){

    }
    public void setFileName(String inFileName){srcFileName = inFileName;}
    public void setDirName(String inDirName){dirName = inDirName;}

    public void getFileName(){System.out.println(srcFileName);}

    public void printMethod(int lineNumber) throws ParseException, IOException {
        File srcRoot = new File(dirName);
        //String srcFilename = "src/document/Document.java";
        File src = new File(srcRoot, srcFileName);
        //System.out.println(f);
        //System.out.println(srcRoot);
        //System.out.println(src);
        getMethodLineNumbers(src, lineNumber);
    }

    private static void getMethodLineNumbers(File src, int lineNumber) throws ParseException, IOException {
        CompilationUnit cu = JavaParser.parse(src);
        new MethodVisitor().visit(cu, lineNumber);
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class MethodVisitor extends VoidVisitorAdapter {
        public void visit(MethodDeclaration m, Object arg) {
            //if (Integer.parseInt(arg.toString() <= )
            String beginLn = m.getBegin().toString();
            String endLn = m.getEnd().toString();
            //System.out.println(endLn.substring(15,beginLn.indexOf(','))
            //        + "->" + Integer.parseInt(arg.toString()));
            if(Integer.parseInt(beginLn.substring(15,beginLn.indexOf(','))) <= Integer.parseInt(arg.toString())
                    && Integer.parseInt(endLn.substring(15,beginLn.indexOf(','))) >= Integer.parseInt(arg.toString())) {
                System.out.println(m.getDeclarationAsString());
                System.out.println(m.getBody().toString().substring(9,m.getBody().toString().lastIndexOf(']')));
                System.out.print("Notes:\n\n\n");
            }

            //System.out.println("From [" + m.getBegin() + "," + m.getBegin() + "] to [" + m.getEnd() + ","
            //        + m.getEnd() + "] is method:");

        }
    }

}
