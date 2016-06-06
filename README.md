# grid-video-recorder
<h3>Grid Video recorder :</h3>
This extension enables to do a video recording of running test cases in a selenium grid , 
For demonstration purpose it has a Screen recorder that uses Monte Media library (http://www.randelshofer.ch/monte/) to create .mov files of the test automation running in selenium node . To view the licenseing information of the recorder please refer to http://www.randelshofer.ch/monte/license.html


<p>Configuration Steps</p>  
<ul>
<li>Create a new folder 
<li>Download the jar (grid-video-recorder.jar) into the folder 
<li>Download the selenium standalone server .jar into the folder 
<li>Download monte media library jar file from http://www.randelshofer.ch/monte/
<li> launch the selenium hub with the following command "java -cp *:. org.openqa.grid.selenium.GridLauncher -role hub"
<li> launch the selenium node with the following command "java -cp *:. org.openqa.grid.selenium.GridLauncher -role node -proxy io.nirvagi.utils.node.proxy.ScreenRecordingProxy -servlets io.nirvagi.utils.servlet.RecorderServlet"
</ul>


<h4>Calling the recorder in the client code</h4>
<p>
The recorder uses two desired capabilities node_screen_recording which must be set to true to enable recording . By default the recorder stops recording after 180 seconds . This can be configured with the desired capabiliy node_recording_timeout
</p>
The following example in java demonstrates how to use the recorder 
```java
public static void main(String args[]) throws Exception{
		DesiredCapabilities caps = DesiredCapabilities.firefox();
		// Enable screen recording 
		caps.setCapability("node_screen_recording", true);
		// Set the recorder timeout to 60 seconds, The Screen recording will be stopped after 60 seconds
		caps.setCapability("node_recording_timeout", 60);
		RemoteWebDriver d = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps);
		d.get("http://www.google.com");
		// Your screen recording would have ended here 
		d.quit();		
	}
```

The recording can be viewed by accessing the node (For example assuming that the node is running in localhost , 
	the video files can be viewed at localhost:5555/extra/RecorderServlet) 

<p>Points to remember</p>
<ul>
<li>The recording happens at OS level , So if two test sessions are run parallely in the same node, The first session to start the recording  will get handle to the recorder and will be able to stop it 
<li>If the browser is minimized, due to some reason  the recording cannot capture what is happening 
