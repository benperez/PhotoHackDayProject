package com.phd3.onesecond;


import java.io.IOException;

import android.content.Context;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.hardware.Camera;

/**
 * 
 * @author openmobster@gmail.com
 */
public class MainActivity extends SurfaceView implements SurfaceHolder.Callback
{
        private SurfaceHolder holder;
        private Camera camera;
        
        public MainActivity(Context context) 
        {
                super(context);
                
                //Initiate the Surface Holder properly
                this.holder = this.getHolder();
                this.holder.addCallback(this);
                this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        
        public void surfaceCreated(SurfaceHolder holder) 
        {
                try
                {
                        //Open the Camera in preview mode
                        this.camera = Camera.open();
                        this.camera.setPreviewDisplay(this.holder);
                }
                catch(IOException ioe)
                {
                        ioe.printStackTrace(System.out);
                }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
        {
                // Now that the size is known, set up the camera parameters and begin
                // the preview.
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(width, height);
                camera.setParameters(parameters);
                camera.startPreview();
        }


        public void surfaceDestroyed(SurfaceHolder holder) 
        {
                // Surface will be destroyed when replaced with a new screen
                //Always make sure to release the Camera instance
                camera.stopPreview();
                camera.release();
                camera = null;
        }
        
        public Camera getCamera()
        {
                return this.camera;
        }
}