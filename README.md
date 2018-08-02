PURPOSE:
  Wanted a simple tool that ran in the background and collected data to verify location processing on the mobile.  Used this to
  verify the GPS on a device that was reporting accuracies in excess of 2200 meters.
  
  Test tool to check gps chip and location processing on a mobile.  Stripped down code focusing on collecting lat, lon and accuracy 
  information.  Simple gui starts, display's and stops the data collection.  GUI's stop button writes data collected to the application's
  files folder for later review.  The display button lauches a scrolling view which shows the data collected.  
  
  Data is not persisted and is cleaned up when the application gui's stop button is clicked and the application is exited.  Simple baseadapter
  is used to display the collected data.  
