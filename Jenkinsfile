
pipeline {
    agent any

    stages {    		
    
        stage('Clean') {
            steps {
			    withMaven(
			    	maven: 'maven-3.5.4', 
			    	jdk: 'openjdk-11_linux-x64',
			    	mavenSettingsFilePath: '/var/jenkins_home/settings.xml',
			    	mavenLocalRepo:'~/.m2/repository' ) {							

    	                sh "mvn clean"
                }
            }
        }
    
		stage('Build') {
        	steps {	
			    withMaven(
			    	maven: 'maven-3.5.4', 
			    	jdk: 'openjdk-11_linux-x64',
			    	mavenSettingsFilePath: '/var/jenkins_home/settings.xml',
			    	mavenLocalRepo:'~/.m2/repository' ) {							

						sh 'mvn -P SkipTests install'
			    }
            }
		}		

		stage('Test') {
        	steps {	
			    withMaven(
			    	maven: 'maven-3.5.4', 
			    	jdk: 'openjdk-11_linux-x64',
			    	mavenSettingsFilePath: '/var/jenkins_home/settings.xml',
			    	mavenLocalRepo:'~/.m2/repository' ) {							

						sh 'mvn -P DoTests -Dmaven.test.failure.ignore=true test'
			    }
            }
            post {
                always {
                    junit '**/target/*.xml'
                }
            }            
		}		

	}		    
}

