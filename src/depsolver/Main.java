package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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
  public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

    // CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
    HashMap<String, String> posConst = new HashMap<String, String>();
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

    }

    
    

    List<Package> installedPacks = new ArrayList<>();
    // Add packages from initial to installed HashMap
    for (String init : initial) {
      String[] temp = init.split("=",2);


      for (Package p : repo) {
        if (p.getName() == temp[0] && p.getVersion() == temp[1]) {
          installedPacks.add(p);
        }
      }
    }

    String commands = "[";



    commands += "]";
    
    System.out.println(commands);
  }

  static boolean isFinal(List<Package> installed, HashMap<String, String> positives, HashMap<String, String> negatives) {
    
    //TODO: Complete isFinal method

    return null;

  }

  public static List<Package> search(List<Package> starting) {

    List<Package> seen = new ArrayList<>();

    // TODO: Complete search method as per Piazza

    return null;
  }

  /*
    isValid takes a package list and checks all dependencies and conflicts are met.
  */
  public static boolean isValid(List<Package> installed) {
    //Dependencies Start
    for (Package p1 : installed) {
      List<List<String>> deps = new ArrayList<List<String>>();
      if (p1.getDepends() != null) {
        deps = p1.getDepends();
      }
      for (List<String> clause : deps) {
        for (String q : clause) {
          if (q.contains(">") && q.contains("=")) {

            String[] grEqSplit = q.split(">=",2);
            boolean flag = false;
            for (Package pGrEq : installed) {
              if (pGrEq.getName() == grEqSplit[0] && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
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

          } else if (q.contains("<") && q.contains("=")) {
            // Less Than or Equal To
            String[] leEqSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pLeEq : installed) {
              if (pLeEq.getName() == leEqSplit[0] && lex(pLeEq.getVersion(), leEqSplit[1]) <= 0  ) {
                flag = true;
              }
            }
            if (!flag) {
              return false;
            }
          } else if (q.contains(">")) {
            // Greater Than
            String[] grSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pGr : installed) {
              if (pGr.getName() == grSplit[0] && lex(pGr.getVersion(), grSplit[1]) > 0  ) {
                flag = true;
              }
            }
            if (!flag) {
              return false;
            }

          } else if (q.contains("<")) {
            // Less Than

            String[] leSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pLe : installed) {
              if (pLe.getName() == leSplit[0] && lex(pLe.getVersion(), leSplit[1]) < 0  ) {
                flag = true;
              }
            }
            if (!flag) {
              return false;
            }
          } else if (q.contains("=")) {
            // Equals

            String[] eqSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pEq : installed) {
              if (pEq.getName() == eqSplit[0] && lex(pEq.getVersion(), eqSplit[1]) == 0  ) {
                flag = true;
              }
            }
            if (!flag) {
              return false;
            }
          } else {
            // Any Version
            for (Package any : installed) {
              boolean flag = false;
              if (any.getName() == q){
                flag = true;
              }
            }

            if (!flag) {
              return false;
            }
          }
        }
      }
    }
    // Dependencies end

    // Conflicts Start

    for (Package p2 : installed) {

      List<List<String>> confs = new ArrayList<List<String>>();
      if (p2.getDepends() != null) {
        confs = p2.getDepends();
      }

      for (List<String> clause : confs) {
        for (String q : clause) {
          if (q.contains(">") && q.contains("=")) {

            String[] grEqSplit = q.split(">=",2);
            boolean flag = false;
            for (Package pGrEq : installed) {
              if (pGrEq.getName() == grEqSplit[0] && lex(pGrEq.getVersion(), grEqSplit[1]) >= 0  ) {
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

          } else if (q.contains("<") && q.contains("=")) {
            // Less Than or Equal To
            String[] leEqSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pLeEq : installed) {
              if (pLeEq.getName() == leEqSplit[0] && lex(pLeEq.getVersion(), leEqSplit[1]) <= 0  ) {
                flag = true;
              }
            }
            if (flag) {
              return false;
            }
          } else if (q.contains(">")) {
            // Greater Than
            String[] grSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pGr : installed) {
              if (pGr.getName() == grSplit[0] && lex(pGr.getVersion(), grSplit[1]) > 0  ) {
                flag = true;
              }
            }
            if (flag) {
              return false;
            }

          } else if (q.contains("<")) {
            // Less Than

            String[] leSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pLe : installed) {
              if (pLe.getName() == leSplit[0] && lex(pLe.getVersion(), leSplit[1]) < 0  ) {
                flag = true;
              }
            }
            if (flag) {
              return false;
            }
          } else if (q.contains("=")) {
            // Equals

            String[] eqSplit = q.split("<=",2);
            boolean flag = false;
            for (Package pEq : installed) {
              if (pEq.getName() == eqSplit[0] && lex(pEq.getVersion(), eqSplit[1]) == 0  ) {
                flag = true;
              }
            }
            if (flag) {
              return false;
            }
          } else {
            // Any Version
            for (Package any : installed) {
              boolean flag = false;
              if (any.getName() == q){
                flag = true;
              }
            }

            if (flag) {
              return false;
            }
          }
        }
      }
    }

    // Conflicts End

    return true;
  }

  /*
    Lex calculates lexicographic comparison and returns the following
    0 if equal
    >0 if v1 is greater
    <0 if v2 is greater
  */
  public static int lex(String v1, String v2) {
    String[] x = v1.split(".");
    String[] y = v2.split(".");

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
      
    } else {
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
