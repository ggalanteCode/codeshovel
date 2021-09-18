package com.felixgrund.codeshovel.scripts;

import java.util.Map;

public class EnvVarExample {

    public static void main (String[] args) {

        ProcessBuilder pb = new ProcessBuilder();
        Map<String, String> env1 = pb.environment();
        env1.put("REPO_DIR", "C:\\Users\\User\\Desktop\\codeshovel-master");
        env1.put("LANG", "java");
        System.out.println("ProcessBuilder environment variables:");
        for (String envName : env1.keySet()) {
            System.out.format("%s=%s%n",
                    envName,
                    env1.get(envName));
        }


        Map<String, String> env2 = System.getenv();
        System.out.println("System environment variables:");
        for (String envName : env2.keySet()) {
            System.out.format("%s=%s%n",
                    envName,
                    env2.get(envName));
        }
    }

}
