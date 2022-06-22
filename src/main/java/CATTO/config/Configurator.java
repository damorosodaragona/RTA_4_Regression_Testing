package CATTO.config;

import java.util.List;

public class Configurator {
    List<String> outputPath;
    List<String> dependencies;

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    String javaVersion;

    public String getTempFolderPath() {
        return tempFolderPath;
    }

    public void setTempFolderPath(String tempFolderPath) {
        this.tempFolderPath = tempFolderPath;
    }

    String tempFolderPath;

    public Configurator(){

    }

    public Configurator(List<String> outputPath, List<String> dependencies, String tempFolderPath, String javaVersion){
        this.outputPath = outputPath;
        this.dependencies = dependencies;
        this.tempFolderPath = tempFolderPath;
        this.javaVersion = javaVersion;


    }

    public List<String> getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(List<String> outputPath) {
        this.outputPath = outputPath;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }





}
