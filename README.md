# Custom Gallery
Gallery like Whatsapp which comes from bottom and opens till top with sticky headers
You can add it to your application using the following


# In project level gradle file




 ```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
  
  # In module level build file
  
  
  
  
  ```groovy
  dependencies {
	        implementation 'com.github.r00786:Custom-Gallery:2.0.5'
	}
  ```
  
  # Usage
  for opening gallery or camera
  
  
  
           
         // first param context
         //second param request code which you will use for receiving result
         //third param is a boolean put true if you want to open gallery and false if you want to open camera 
         //selection count is the number of count you allow a user to select images at a time
         
        GalleryAct.openGallery(this, 101, true, 1);
	
	
 ```groovy	
	  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {

            ArrayList<String> imagePath = data.getStringArrayListExtra(GalleryAct.IMAGE_KEY);
            ivSample.setImageURI(Uri.parse(imagePath.get(0)));
        }
    }
    
``` 
  

