package com.gpp.devoluciondeenvases.principal.principal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.LruCache;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gpp.devoluciondeenvases.R;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class activity that allows users to browse through files and folders
 */
public class FileBrowseActivity extends AppCompatActivity {

	//Keys for file and directory
    private final static String DEFAULT_INITIAL_DIRECTORY = Environment.getExternalStorageDirectory().getParentFile().getParent();
    private final static String INTERNAL_STORAGE = "Internal Storage";
    private final static String EXTERNAL_STORAGE = "External Storage";


    protected File currentFile;
    protected ArrayList<File> filesList;
    protected FileBrowseListAdapter fileAdapter;
    protected boolean showHidden = false;

    private LruCache<String, Bitmap> mMemoryCache;

    Button selectButton;
    //Button newFileButton;
    //Button newFolderButton;
    Button backButton;
    ListView listView;
    TextView currentPath;
    //EditText inputEditText;
   // AlertDialog.Builder m_AlertBuilder;
    //AlertDialog m_AlertDialog;
    private String fileName;
    private String filePath;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_view_main);

        // Prompt Android 6 user if permission is not yet granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writeExternalPermission != PackageManager.PERMISSION_GRANTED || readExternalPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }

        //List
        listView = (ListView)findViewById(android.R.id.list);

        // Set the view to be shown if the list is empty
        LayoutInflater inflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyView = inflator.inflate(R.layout.file_empty_view, (ViewGroup) listView.getParent(), false); // .inflate(R.layout.file_empty_view, null);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);

        LinearLayout root = (LinearLayout)listView.getParent();
        Toolbar bar = (Toolbar)root.findViewById(R.id.toolbar_browse);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set initial directory
        currentFile = new File(DEFAULT_INITIAL_DIRECTORY);
        currentPath = (TextView)findViewById(R.id.current_folder_path);
        if (currentPath != null)
            currentPath.setText(DEFAULT_INITIAL_DIRECTORY);
        // Initialize the ArrayList
        filesList = new ArrayList<>();

        // Set the ListAdapter
        fileAdapter = new FileBrowseListAdapter(this, filesList);
        listView.setAdapter(fileAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File newFile = (File)parent.getItemAtPosition(position);

                //if selected object is a file and not a folder then pass data back to main activity
                if(newFile.isFile()) {
                    currentFile = newFile;
                    // Update path of current folder
                    currentPath= (TextView)findViewById(R.id.current_folder_path);
                    currentPath.setText(currentFile.getAbsolutePath());

                    File object = currentFile;
                    if(object != null && object.isFile())
                    {
                        Intent intent = new Intent();
                        fileName = object.getName();
                        filePath = object.getAbsolutePath();

                        intent.putExtra(Configuracion.FOLDER_NAME_KEY,fileName);
                        intent.putExtra(Configuracion.FOLDER_PATH_KEY,filePath);
                        setResult(RESULT_OK, intent);

                        // Finish the activity
                        finish();
                    }
                }
                //if item selected is a directory
                else {
                    currentFile = newFile;
                    // Update path of current folder
                    currentPath= (TextView)findViewById(R.id.current_folder_path);
                    currentPath.setText(currentFile.getAbsolutePath());
                    // Update the files list
                    refreshFilesList();
                    //Scroll back to top
                    ((ListView)parent).setSelectionAfterHeaderView();
                }
            }
        });
        

        selectButton = (Button) findViewById(R.id.select_folder_button);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File object = currentFile;
                if (object != null && object.isDirectory()) {
                    Intent intent = new Intent();
                    fileName = object.getName();
                    filePath = object.getAbsolutePath();
                    intent.putExtra(Configuracion.FOLDER_NAME_KEY, fileName);
                    intent.putExtra(Configuracion.FOLDER_PATH_KEY, filePath);
                    setResult(RESULT_OK, intent);
                    // Finish the activity
                    finish();
                }
            }
        });

        // ---------------------------------------
		// handler for the back button click
		// ---------------------------------------
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(currentFile.getParentFile() != null && !currentFile.getParentFile().getAbsolutePath().equals("/")) {
                    //This is internal sd card
                    if(currentFile.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                        currentFile = currentFile.getParentFile().getParentFile();
                    }
                    else {
                        // Go to parent directory
                        currentFile = currentFile.getParentFile();
                    }
                    currentPath.setText(currentFile.getAbsolutePath());
                    refreshFilesList();
		        }
				else
					finish();
			}
		});


        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        
        // Get intent extras
        if(getIntent().hasExtra(Configuracion.FOLDER_PATH_KEY)) {
            currentFile = new File(getIntent().getStringExtra(Configuracion.FOLDER_PATH_KEY));
        }
    }

    @Override
    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Updates the list view to the current directory
     */
    protected void refreshFilesList() {
        // Clear the files ArrayList
        filesList.clear();

        // For default storage root
        if(currentFile.getAbsolutePath().equals(DEFAULT_INITIAL_DIRECTORY)) {
            //Add internal storage
            File internalStorage = Environment.getExternalStorageDirectory();
            filesList.add(internalStorage);

            //Add external storage
            File externalStorage = getRemovableStorage();
            if (externalStorage != null)
                filesList.add(externalStorage);
        }

        //This is not storage root
        else {
            File[] files = currentFile.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if ((f.isHidden() && !showHidden)) {
                        // Don't add the file
                        continue;
                    }
                    // Add the file the ArrayAdapter
                    filesList.add(f);
                }
            }
        }
        Collections.sort(filesList, new FileComparator());
        fileAdapter.notifyDataSetChanged();
    }


    /**
     * Uses the Environmental variable "SECONDARY_STORAGE" to locate a removable micro sdcard
     * @return  the primary secondary storage directory or
     *          {@code null} if there is no removable storage
     */
    public static File getRemovableStorage() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String value = System.getenv("SECONDARY_STORAGE");
            if (value == null || value.length() == 0)
                value = System.getenv("EXTERNAL_SDCARD_STORAGE");
            if (!TextUtils.isEmpty(value)) {
                final String[] paths = value.split(":");
                for (String path : paths) {
                    File file = new File(path);
                    if (file.isDirectory()) {
                        return file;
                    }
                }
            }
        }
        //For Marshmallow
        else {
            File[] files = Environment.getExternalStorageDirectory().getParentFile().getParentFile().listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    //Add External
                    Pattern pattern = Pattern.compile("([0-9A-F]{4}-[0-9A-F]{4})");
                    Matcher matcher = pattern.matcher(file.getAbsolutePath());
                    if (matcher.find()) {
                        return file;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a file is on the removable SD card.
     * @see {@link Environment#isExternalStorageRemovable()}
     * @param file a {@link File}
     * @return {@code true} if file is on a removable micro SD card, {@code false} otherwise
     */
    public static boolean isFileOnRemovableStorage(File file) {
        boolean ret = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ret = Environment.isExternalStorageRemovable(file);
        }
        else {
            final File microSD = getRemovableStorage();
            if (microSD != null) {
                String canonicalPath;
                try {
                    canonicalPath = file.getCanonicalPath();
                    if (canonicalPath.startsWith(microSD.getAbsolutePath())) {
                        ret = true;
                    }
                } catch (IOException ignore) {
                }
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        if(currentFile.getParentFile() != null && !currentFile.getParentFile().getAbsolutePath().equals("/")) {

            //This is internal sd card
            if(currentFile.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                currentFile = currentFile.getParentFile().getParentFile();
            }
            else {
                // Go to parent directory
                currentFile = currentFile.getParentFile();
            }
            currentPath.setText(currentFile.getAbsolutePath());
            refreshFilesList();
            return;
        }
        super.onBackPressed();
    }

    /**File browse list adapter class used to display**/
    private class FileBrowseListAdapter extends ArrayAdapter<File> {

        private List<File> mObjects;
        Bitmap mPlaceHolderBitmap;

        public FileBrowseListAdapter(Context context, List<File> objects) {
            super(context, R.layout.file_list_item,android.R.id.text1, objects);
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row;

            if(convertView == null) { 
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.file_list_item, parent, false);
            } 
            else {
                row = convertView;
            }
            
            File object = mObjects.get(position);
            ImageView imageView = (ImageView)row.findViewById(R.id.file_picker_image);
            TextView textView = (TextView)row.findViewById(R.id.file_picker_text);

            // Set single line
            textView.setSingleLine(true);

            //This is internal storage
            if (object.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                textView.setText(INTERNAL_STORAGE);
            }
            //This is external storage
            else if (object.equals(getRemovableStorage())) {
                textView.setText(EXTERNAL_STORAGE);
            }
            //All other files
            else {
                textView.setText(object.getName());
            }
            if(object.isFile()) {

            	//Check if it is an image
            	String[] okFileExtensions =  new String[] {".jpg", ".png", ".gif",".jpeg",".bmp", ".tif", ".tiff",".pcx"};
            	boolean isImage = false;
            	for (String extension : okFileExtensions) {
					if(object.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith(extension))
					{
						isImage = true;
						break;
					}
				}
            	if (isImage) {
                    Bitmap bitmap = getBitmapFromMemCache(object.getName());
                    if(bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                    else {
                        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                        mPlaceHolderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.file);
                        final AsyncDrawable asyncDrawable =
                                new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
                        imageView.setImageDrawable(asyncDrawable);
                        task.execute(object);
                    }
                  // Bitmap bitmap = decodeSampledBitmapFromFile(object.getAbsolutePath(), 50, 50);
            		//imageView.setImageBitmap(bitmap);
				}
            	else if (object.getAbsolutePath().endsWith(".pdf"))
            	{
            		// Show the file icon
	                imageView.setImageResource(R.drawable.file_pdf);
            	}
            	else
            	{
	                // Show the file icon
	                imageView.setImageResource(R.drawable.file);
            	}
            } else {
                // Show the folder icon
                imageView.setImageResource(R.drawable.folder);
            }
            return row;
        }
    }


    /**Class used for sorting files in File list main activity**/
    private class FileComparator implements Comparator<File>, Serializable {
        private static final long serialVersionUID = -2777990725027370252L;

        public int compare(File f1, File f2) {
            if(f1 == f2) {
                return 0;
            }
            if(f1.isDirectory() && f2.isFile()) {
                // Show directories above files
                return -1;
            }
            if(f1.isFile() && f2.isDirectory()) {
                // Show files below directories
                return 1;
            }
            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }



    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            final Bitmap bitmap = decodeSampledBitmapFromFile(params[0].getAbsolutePath(), 100, 100);

            if(bitmap != null)
                addBitmapToMemoryCache(params[0].getName(), bitmap);
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }


    }

}