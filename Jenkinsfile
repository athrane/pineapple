
pipeline {
    agent any

    stages {    		
    
        stage('Clean') {
            steps {
			    withMaven(maven: 'maven-3.5.4', jdk: 'openjdk-11_linux-x64') {							
                    sh "echo JAVA_HOME=$JAVA_HOME"
                    sh "mvn clean"
                }
            }
        }
    
		stage('Build') {
        	steps {	
			    withMaven(maven: 'maven-3.5.4', jdk: 'openjdk-11_linux-x64') {							
					sh 'mvn -DskipTests clean install'
			    }
            }
		}		
	}		    
}

