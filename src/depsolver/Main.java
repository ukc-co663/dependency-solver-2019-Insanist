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

    // Store positive and negative constraints to check against when installing
    for (String i : constraints) {
      if (i.charAt(0) == '+') {
        String[] posTemp = i.split("=",2);
        posConst.put(posTemp[0], posTemp[1]);
      } else if (i.charAt(1) == '-') {
        String[] negTemp = i.split("=",2);
        negConst.put(negTemp[0], negTemp[1]);
      }
    }
    

    HashMap<String, String> installed = new HashMap<>();
    // Add packages from initial to installed HashMap
    for (String init : initial) {
      String[] temp = init.split("=",2);
      installed.put(temp[0], temp[1]);
    }

    String commands = "[";

    // 1. Start with constraint package
    // 2. DFS traverse from start package
    //  2a. Cycle through deps in alpha order


    System.out.println(posConst.size());
    boolean isFirst = true;
    for (Package p : repo) {
      // Attempt at seen 9
      // If package isnt a negative constraint - install it
      if (!negConst.containsKey(p.getName())) {
        installed.put(p.getName(),p.getVersion());
        if (isFirst) {
          commands += p.getName() + "=" + p.getVersion();
          isFirst = false;
        } else {
          commands += ",\"" + p.getName() + "=" + p.getVersion() + "\"";
        }
      }
    }

    commands += "]";
    
    System.out.println(commands);
  }

  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }
}
