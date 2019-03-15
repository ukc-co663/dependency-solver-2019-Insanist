package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Collections;

class Package {
  private String name;
  private String version;
  private Integer size;
  private List<List<String>> depends = new ArrayList<>();
  private List<String> conflicts = new ArrayList<>();

  public String getName() { return name; }
  public String getVersion() { return version; }
  public Integer getSize() { return size; }
  public List<List<String>> getDepends() { return depends; }
  public List<String> getConflicts() { return conflicts; }
  public void setName(String name) { this.name = name; }
  public void setVersion(String version) { this.version = version; }
  public void setSize(Integer size) { this.size = size; }
  public void setDepends(List<List<String>> depends) { this.depends = depends; }
  public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }
}

public class Main {
static List<String> constraintsMa = new ArrayList<>();
  public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

    constraintsMa = constraints;
    // CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
   /*  HashMap<String, String> posConst = new HashMap<String, String>();
    HashMap<String, String> negConst = new HashMap<String, String>();

    //DFS components
    Stack<Package> dfsStack = new Stack<>();
    List<String> dfsVisited = new ArrayList<>();

    // Store positive and negative constraints to check against when installing/uninstalling
    if (constraints.size() != 0) {

      for (String i : constraints) {
        if (i.charAt(0) == '+') {
          String[] posTemp = i.split("=",2);
          posConst.put(posTemp[0], posTemp[1]);
        } else if (i.charAt(1) == '-') {
          String[] negTemp = i.split("=",2);
          negConst.put(negTemp[0], negTemp[1]);
        }
      }

    } */

    
/*     ArrayList<Package> finalTest = new ArrayList<Package>();
    finalTest.add(repo.get(3));
    finalTest.add(repo.get(4));
    finalTest.add(repo.get(0));
    System.out.println("FinalTest");
    for (Package p : finalTest) {
      System.out.println(p.getName() + " " + p.getVersion());
    }
    System.out.println("Final Test Result: " + isFinal(finalTest)); */


    ArrayList<Package> installedPacks = new ArrayList<>();
    // Add packages from initial to installed List
    for (String init : initial) {
      String[] temp = init.split("=",2);
     // System.out.println(temp[0]);
      //System.out.println(temp[1]);

      for (Package p : repo) {
        //System.out.println(p.getName() + " " + p.getVersion());
        if (p.getName().equals(temp[0]) && p.getVersion().equals(temp[1])) {
          installedPacks.add(p);
        }
      }
    }

 /*    if (installedPacks.size() == 0) {
      Package emptyPack  = new Package();
      installedPacks.add(emptyPack);
    } */


    // SEEN 0
    Package benchmark = repo.get(0);
    Package comparePack = new Package();
    
    Package lowestPack = repo.get(0);



    for (Package pack : repo) {
      comparePack = pack;
      if (pack.getSize() < lowestPack.getSize() ) {
        lowestPack = pack;
      }
    }

    if (benchmark.getName().equals(comparePack.getName())) {
      String instLowest = "[+" + lowestPack.getName() + "=" + lowestPack.getVersion() + "]";
      System.out.println(JSON.toJSONString(instLowest, true));

      // END SEEN 0
    } else {
    
    ArrayList<String> emptySet = new ArrayList<String>();
    search(installedPacks, repo, emptySet);


    
    //System.out.println("Final cmds: " + finalCmds);
    Collections.reverse(finalCmds);
    String res = JSON.toJSONString(finalCmds, true);
    System.out.println(res);
    }


    
    


  }

  static boolean isFinal(List<Package> installed) {
    
    //TODO: Complete isFinal method
    // 1. Iterate over constraints list
    // 2. Split on comparison operators (branches for each combo)
    // 3. .
    List<String> constr = constraintsMa;
    for (String s : constr) {
      String c = "";
      if (s.charAt(0) == '+') { // Positive constraint
        c = s.substring(1);
        if (c.contains(">") && c.contains("=")) {

          String[] grEqSplit = c.split(">=",2);
          boolean flag = false;
          for (Package pGrEq : installed) {
            if (pGrEq.getName().equals(grEqSplit[0]) && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
              flag = true;
            }
          }
          if (!flag) {
            return false;
          }

          // Greater than or Equal To
          // 1. Split into Package name and version
          // 2. Loop through all installed packages
          // 3. Find version name and check for correct version with compareTo()
          // 4. If found set local oolean to true and continue looping
          // 5. If not found immediately return FALSE overall

        } else if (c.contains("<") && c.contains("=")) {
          // Less Than or Equal To
          
          String[] leEqSplit = c.split("<=",2);
          boolean flag = false;
          for (Package pGrEq : installed) {
            if (pGrEq.getName().equals(leEqSplit[0]) && lex(pGrEq.getVersion(), leEqSplit[1]) <= 0  ) {
              flag = true;
            }
          }
          if (!flag) {
            return false;
          }
        } else if (c.contains(">")) {
          // Greater Than
          
          String[] grSplit = c.split(">",2);
          boolean flag = false;
          for (Package pGrEq : installed) {
            if (pGrEq.getName().equals(grSplit[0]) && lex(pGrEq.getVersion(), grSplit[1]) > 0  ) {
              flag = true;
            }
          }
          if (!flag) {
            return false;
          }
        } else if (c.contains("<")) {
          // Less Than

          String[] leSplit = c.split("<",2);
          boolean flag = false;
          for (Package pGrEq : installed) {
            if (pGrEq.getName().equals(leSplit[0]) && lex(pGrEq.getVersion(), leSplit[1]) < 0  ) {
              flag = true;
            }
          }
          if (!flag) {
            return false;
          }
        } else if (c.contains("=")) {
          // Equals
          String[] eqSplit = c.split("=",2);
          boolean flag = false;
          for (Package pGrEq : installed) {
            if (pGrEq.getName().equals(eqSplit[0]) && lex(pGrEq.getVersion(), eqSplit[1]) == 0  ) {
              flag = true;
            }
          }
          if (!flag) {
            return false;
          }
        } else {
          // Any Version
          boolean flag = false;
          for (Package any : installed) {
            
            if (any.getName().equals(c)){
              flag = true;
            }
          }

          if (!flag) {
            return false;
          }
        }
        //System.out.println("Positive constraints are final");
      } else if (s.charAt(0) == '-') { // Negative constraint
        c = s.substring(1);
        if (c.contains(">") && c.contains("=")) {

          String[] grEqSplit = c.split(">=",2);
          boolean flag = false;
          for (Package pGrEq : installed) {
            if (pGrEq.getName().equals(grEqSplit[0]) && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
              flag = true;
            }
          }
          if (flag) {
            return false;
          }

          // Greater than or Equal To
          // 1. Split into Package name and version
          // 2. Loop through all installed packages
          // 3. Find version name and check for correct version with compareTo()
          // 4. If found set local oolean to true and continue looping
          // 5. If not found immediately return FALSE overall

        } else if (c.contains("<") && c.contains("=")) {
          // Less Than or Equal To
          String[] leEqSplit = c.split("<=",2);
          boolean flag = false;
          for (Package pLeEq : installed) {
            if (pLeEq.getName().equals(leEqSplit[0]) && lex(pLeEq.getVersion(), leEqSplit[1]) <= 0  ) {
              flag = true;
            }
          }
          if (flag) {
            return false;
          }
        } else if (c.contains(">")) {
          // Greater Than
          String[] grSplit = c.split(">",2);
          boolean flag = false;
          for (Package pGr : installed) {
            if (pGr.getName().equals(grSplit[0]) && lex(pGr.getVersion(), grSplit[1]) > 0  ) {
              flag = true;
            }
          }
          if (flag) {
            return false;
          }

        } else if (c.contains("<")) {
          // Less Than

          String[] leSplit = c.split("<",2);
          boolean flag = false;
          for (Package pLe : installed) {
            if (pLe.getName().equals(leSplit[0]) && lex(pLe.getVersion(), leSplit[1]) < 0  ) {
              flag = true;
            }
          }
          if (flag) {
            return false;
          }
        } else if (c.contains("=")) {
          // Equals

          String[] eqSplit = c.split("=",2);
          boolean flag = false;
          for (Package pEq : installed) {
            if (pEq.getName().equals(eqSplit[0]) && lex(pEq.getVersion(), eqSplit[1]) == 0  ) {
              flag = true;
            }
          }
          if (flag) {
            return false;
          }
        } else {
          // Any Version
          boolean flag = false;
          for (Package any : installed) {
            
            if (any.getName().equals(c)){
              flag = true;
            }
          }

          if (flag) {
            return false;
          }
        }
      }
    }
    //System.out.println("Negative constraints are final");
    return true;

  }

  
  static HashSet<ArrayList<Package>> seen2 = new HashSet();
  static ArrayList<Package> solution = new ArrayList();
  static boolean solFound = false;
  static ArrayList<String> finalCmds = new ArrayList<String>();


  public static void search(ArrayList<Package> x, List<Package> repo, ArrayList<String> cmds) {
    ArrayList<String> tempCmds = new ArrayList<String>(cmds);
    
    if (!solFound) {
      // TODO: Complete search method as per Piazza

      if (isValid(x)) {
    
        

      boolean seenTemp = seen2.contains(x);
      
      if (!seenTemp) {
        System.out.println(tempCmds);
        System.out.println("\n");
        for (Package x1 : x) {
          System.out.println(x1.getName() + " " + x1.getVersion());
        }
        System.out.println("Is Valid & Unseen");
        

        seen2.add(x);
        if (isFinal(x)) {          
          
          /* for (String s : tempCmds) {
            finalCmds.add(s);
          }
 */
          solFound = true;
          /* System.out.println("Solution");
          for (Package sol : x) {
            System.out.println(sol.getName() + " " + sol.getVersion());
          }
          System.out.println("\n"); */
          
          solution = x;

        } else {
          
          for (Package p : repo) {
            if (!solFound) {
            ArrayList<Package> y = new ArrayList<Package>(x);
           
            if (!y.contains(p)) {
              y.add(p);
              
              ArrayList<String> plusCmds = new ArrayList<String>(tempCmds);
              String plusPkg = "+" + p.getName() + "=" + p.getVersion();
              //System.out.println("Plus: " + plusPkg);
              plusCmds.add(plusPkg);
              search(y, repo, plusCmds);
              if (solFound) {
                finalCmds.add(plusPkg);
              }           
            } else if (y.contains(p)) {
              
              y.remove(p);
              
              ArrayList<String> minusCmds = new ArrayList<String>(tempCmds);
              String minusPkg = "-" + p.getName() + "=" + p.getVersion();
              //System.out.println("Minus: " + minusPkg);
              minusCmds.add(minusPkg);
              search(y, repo, minusCmds);
              if (solFound) {
                finalCmds.add(minusPkg);
              }   
            }
           }
            
          }
        }

      } else {
        //System.out.println("Not valid");
      }
    }
  }
    
    
    
  }

  /*
    isValid takes a package list and checks all dependencies and conflicts are met.
    @Returns: True if all deps and confs are met, false in any other case
  */
  public static boolean isValid(List<Package> installed) {
    //Dependencies Start
    //System.out.print("Testing [ ");
    /* for (Package tst : installed) {
      System.out.print(tst.getName()+"="+tst.getVersion() + ", ");
    } */
    //System.out.print("] ");

    for (Package p1 : installed) {
      List<List<String>> deps;
      if (p1.getDepends() != null) {
         deps = new ArrayList<List<String>>(p1.getDepends());
      } else {
        deps = new ArrayList<List<String>>();
      }
      
      
      for (List<String> clause : deps) {
        boolean isDepClauseValid = true;


        // If clause size is greater than 1 - implies OR clause
        
        
        if (clause.size() > 1) {
          //System.out.println( p1.getName() + " OR Clause size: " + clause.size());
          isDepClauseValid = false;
          // Loop through clause and if found a valid section, set to true
          for (String q : clause) {
            
           
            if (q.contains(">") && q.contains("=")) {
  
              String[] grEqSplit = q.split(">=",2);
              boolean flag = false;
              //System.out.println(p1.getName() + " " + p1.getVersion() + " " + q);
              for (Package pGrEq : installed) {
                //System.out.println(grEqSplit[0] + " " + pGrEq.getName());
                if (pGrEq.getName().equals(grEqSplit[0]) && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                System.out.println("isValid grEq");
                return false;
              } */
  
              // Greater than or Equal To
              // 1. Split into Package name and version
              // 2. Loop through all installed packages
              // 3. Find version name and check for correct version with compareTo()
              // 4. If found set local oolean to true and continue looping
              // 5. If not found immediately return FALSE overall
  
            } else if (q.contains("<") && q.contains("=")) {
              // Less Than or Equal To
              String[] leEqSplit = q.split("<=",2);
              boolean flag = false;
              for (Package pLeEq : installed) {
                if (pLeEq.getName().equals(leEqSplit[0]) && lex(pLeEq.getVersion(), leEqSplit[1]) <= 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                System.out.println("isValid leEq");
                return false;
              } */
            } else if (q.contains(">")) {
              // Greater Than
              String[] grSplit = q.split(">",2);
              boolean flag = false;
              for (Package pGr : installed) {
                if (pGr.getName().equals(grSplit[0]) && lex(pGr.getVersion(), grSplit[1]) > 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                return false;
              } */
  
            } else if (q.contains("<")) {
              // Less Than
  
              String[] leSplit = q.split("<",2);
              boolean flag = false;
              
              for (Package pLe : installed) {
                if (pLe.getName().equals(leSplit[0]) && lex(pLe.getVersion(), leSplit[1]) < 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                  //System.out.println(q);
                  //System.out.println(leSplit[0] + " " + leSplit[1] + " " + pLe.getName() +  " " + pLe.getVersion());
                }
              }
              /* if (!flag) {
                System.out.println("isValid gr");
                return false;
              } */
            } else if (q.contains("=")) {
              // Equals
              
              String[] eqSplit = q.split("=",2);
              boolean flag = false;
              for (Package pEq : installed) {
                if (pEq.getName().equals(eqSplit[0]) && lex(pEq.getVersion(), eqSplit[1]) == 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                System.out.println("isValid eq");
                return false;
              } */
            } else {
              // Any Version
              //System.out.println(p1.getName() + "  dep: " +  q);
              boolean flag = false;
              for (Package any : installed) {
                
                if (any.getName().equals(q)){
                  //System.out.println("true");
                  flag = true;
                  isDepClauseValid = true;
                }
              }
  
              /* if (!flag) {
                System.out.println("isValid ANY");
                return false;
              } */
            }
          }

        } else {
          for (String q : clause) {
            isDepClauseValid = false; // valid
           
            if (q.contains(">") && q.contains("=")) {
  
              String[] grEqSplit = q.split(">=",2);
              boolean flag = false;
              //System.out.println(p1.getName() + " " + p1.getVersion() + " " + q);
              for (Package pGrEq : installed) {
                //System.out.println(grEqSplit[0] + " " + pGrEq.getName());
                if (pGrEq.getName().equals(grEqSplit[0]) && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                System.out.println("isValid grEq");
                return false;
              } */
  
              // Greater than or Equal To
              // 1. Split into Package name and version
              // 2. Loop through all installed packages
              // 3. Find version name and check for correct version with compareTo()
              // 4. If found set local oolean to true and continue looping
              // 5. If not found immediately return FALSE overall
  
            } else if (q.contains("<") && q.contains("=")) {
              // Less Than or Equal To
              String[] leEqSplit = q.split("<=",2);
              boolean flag = false;
              for (Package pLeEq : installed) {
                if (pLeEq.getName().equals(leEqSplit[0]) && lex(pLeEq.getVersion(), leEqSplit[1]) <= 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                System.out.println("isValid leEq");
                return false;
              } */
            } else if (q.contains(">")) {
              // Greater Than
              String[] grSplit = q.split(">",2);
              boolean flag = false;
              for (Package pGr : installed) {
                if (pGr.getName().equals(grSplit[0]) && lex(pGr.getVersion(), grSplit[1]) > 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                return false;
              } */
  
            } else if (q.contains("<")) {
              // Less Than
  
              String[] leSplit = q.split("<",2);
              boolean flag = false;
              
              for (Package pLe : installed) {
                if (pLe.getName().equals(leSplit[0]) && lex(pLe.getVersion(), leSplit[1]) < 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                  //System.out.println(q);
                  //System.out.println(leSplit[0] + " " + leSplit[1] + " " + pLe.getName() +  " " + pLe.getVersion());
                }
              }
              /* if (!flag) {
                System.out.println("isValid gr");
                return false;
              } */
            } else if (q.contains("=")) {
              // Equals
              
              String[] eqSplit = q.split("=",2);
              boolean flag = false;
              for (Package pEq : installed) {
                if (pEq.getName().equals(eqSplit[0]) && lex(pEq.getVersion(), eqSplit[1]) == 0  ) {
                  flag = true;
                  isDepClauseValid = true;
                }
              }
              /* if (!flag) {
                System.out.println("isValid eq");
                return false;
              } */
            } else {
              // Any Version
              //System.out.println(p1.getName() + "  dep: " +  q);
              boolean flag = false;
              for (Package any : installed) {
                
                if (any.getName().equals(q)){
                  //System.out.println("true");
                  flag = true;
                  isDepClauseValid = true;
                }
              }
  
              /* if (!flag) {
                System.out.println("isValid ANY");
                return false;
              } */
            }
          }
        }




        
         // why1
        //System.out.println("isDepClauseValid " + isDepClauseValid);
        if (!isDepClauseValid) {
          return false;
        }
      }

      

    }
    // Dependencies end
    //System.out.println("Dependencies valid");
    // Conflicts Start

    for (Package p2 : installed) {

      List<String> confs;
      if (p2.getConflicts() != null) {
        confs = new ArrayList<String>( p2.getConflicts());
      } else {
        confs = new ArrayList<String>();
      }

      for (String q : confs) {
        
          if (q.contains(">") && q.contains("=")) {

            String[] grEqSplit = q.split(">=",2);
            boolean flag = false;
            for (Package pGrEq : installed) {
              if (pGrEq.getName().equals(grEqSplit[0]) && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
                flag = true;
                
              }
            }
            if (flag) {
              //System.out.println("isValid conf grEq");
              return false;
            }

            // Greater than or Equal To
            // 1. Split into Package name and version
            // 2. Loop through all installed packages
            // 3. Find version name and check for correct version with compareTo()
            // 4. If found set local oolean to true and continue looping
            // 5. If not found immediately return FALSE overall

          } else if (q.contains("<") && q.contains("=")) {
            // Less Than or Equal To
            String[] leEqSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pLeEq : installed) {
              if (pLeEq.getName().equals(leEqSplit[0]) && lex(pLeEq.getVersion(), leEqSplit[1]) <= 0  ) {
                flag = true;
                
              }
            }
            if (flag) {
             // System.out.println("isValid conf leEq");
              return false;
            }
          } else if (q.contains(">")) {
            // Greater Than
            String[] grSplit = q.split(">",2);
            boolean flag = false;
            for (Package pGr : installed) {
              if (pGr.getName().equals(grSplit[0]) && lex(pGr.getVersion(), grSplit[1]) > 0  ) {
                flag = true;
                
              }
            }
            if (flag) {
              //System.out.println("isValid conf gr");
              return false;
            }

          } else if (q.contains("<")) {
            // Less Than

            String[] leSplit = q.split("<",2);
           /*  System.out.println("test");
            System.out.println(q);
            System.out.println(leSplit[0] + " " + leSplit[1]);
 */
            
            boolean flag = false;
            
            for (Package pLe : installed) {
              /* //System.out.println(pLe.getVersion());
              System.out.println(leSplit[0]);
              System.out.println(pLe.getName());
              //System.out.println(lex(pLe.getVersion(), leSplit[1])); */
              
              
              if (pLe.getName().equals(leSplit[0]) && lex(pLe.getVersion(), leSplit[1]) < 0) {
                // System.out.println(pLe.getName() +  " " + pLe.getVersion());
                flag = true;
                
              }
            }
            if (flag) {
              //System.out.println("isValid conf le");
              return false;
            }
          } else if (q.contains("=")) {
            // Equals

            String[] eqSplit = q.split("=",2);
            boolean flag = false;
            for (Package pEq : installed) {
              if (pEq.getName().equals(eqSplit[0]) && lex(pEq.getVersion(), eqSplit[1]) == 0  ) {
                flag = true;
                
              }
            }
            if (flag) {
              //System.out.println("isValid conf eq");
              return false;
            }
          } else {
            // Any Version
            boolean flag = false;
            for (Package any : installed) {
              
              if (any.getName().equals(q)) {
                flag = true;
                
              }
            }

            if (flag) {
              //System.out.println("isValid conf any");
              return false;
            }
          }
        }
      
    }

    // Conflicts End
    //System.out.println("Conflicts valid");
    return true;
  }

  /*
    Lex calculates lexicographic comparison and returns the following
    0 if equal
    >0 if v1 is greater
    <0 if v2 is greater
  */
  public static int lex(String v1, String v2) {

    String[] x = v1.split("\\.");
    String[] y = v2.split("\\.");

 /*    for (String bit : x) {
      System.out.println(bit);
    } */


    if (x.length > y.length) {
      for (int i = 0; i < y.length; i ++) {
        if (Integer.parseInt(x[i]) > Integer.parseInt(y[i])) {
          return 1;
        } else if (Integer.parseInt(x[i]) < Integer.parseInt(y[i])) {
          return -1;
        }
      }
      return 1;
    } else if (x.length < y.length) {
      for (int i = 0; i < x.length; i ++) {
        if (Integer.parseInt(x[i]) > Integer.parseInt(y[i])) {
          return 1;
        } else if (Integer.parseInt(x[i]) < Integer.parseInt(y[i])) {
          return -1;
        }
      }
      return -1;
      
    } else if (x.length == y.length) {
      for (int i = 0; i < y.length; i ++) {
        if (Integer.parseInt(x[i]) > Integer.parseInt(y[i])) {
          return 1;
        } else if (Integer.parseInt(x[i]) < Integer.parseInt(y[i])) {
          return -1;
        }
      }
    }
    return 0;    
    
  }

  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }
}
