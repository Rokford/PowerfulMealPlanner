package com.mobinautsoftware.powerfulmealplanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class RecipeListActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private ListView drawerList;

    private ListView recipeListView;

    private RecipeListAdapter adapter;

    GoogleApiClient mGoogleApiClient;

    private boolean forCalendar = false;

    private DriveId tempDriveID;

    private final static int RESOLVE_CONNECTION_REQUEST_CODE = 5;

    //SparseBooleanArray selected;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("forCalendar") != null)
            forCalendar = true;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new NavigationDrawerAdapter(this));

        getSupportActionBar().setTitle(getResources().getString(R.string.recipies_list));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.yes, R.string.no)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                String value = (String) adapter.getItemAtPosition(position);
                if (value == getResources().getString(R.string.recipies_list))
                {
                    Intent intent = new Intent(RecipeListActivity.this, RecipeListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.shopping_list))
                {
                    Intent intent = new Intent(RecipeListActivity.this, ShoppingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.callendar))
                {
                    Intent intent = new Intent(RecipeListActivity.this, CalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.help))
                {
                    Intent intent = new Intent(RecipeListActivity.this, HelpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        recipeListView = (ListView) findViewById(R.id.recipeListView);
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter2, View v, int position, long arg3)
            {
                //String value = (String) adapter.getItem(position);
                Intent intent = new Intent(RecipeListActivity.this, AddRecipeItemActivity.class);
                String selectedItem = (String) (adapter2.getAdapter()).getItem(position);
                intent.putExtra("recipe_name", selectedItem);
                startActivity(intent);
            }
        });

        //DELETING with multiple choice listener------------------------------------------------------------
        recipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                final int checkedCount = recipeListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.menu_delete:
                        deleteSelectedItems();
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                onResume();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.recipe_list_menu, menu);

        if (!forCalendar)
        {
            //            menu.getItem(1).setVisible(false);
            menu.getItem(1).setTitle(getResources().getString(R.string.export));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        }
        else if (item.getItemId() == R.id.menu_new)
        {
            Intent intent = new Intent(this, AddRecipeItemActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_add_checked)
        {
            if (forCalendar)
            {
                Boolean[] checkedRecipes = adapter.getCheckedRecipes();

                DatabaseManager manager = new DatabaseManager(RecipeListActivity.this);

                manager.open();

                for (int i = 0; i < checkedRecipes.length; i++)
                {
                    if (checkedRecipes[i])
                    {
                        manager.addRecipeToDateTable(adapter.getItem(i), getIntent().getStringExtra("dateFromCalendar"));
                    }
                }

                manager.close();

                Intent returnIntent = new Intent();
                //            returnIntent.putExtra("result",result);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            else
            {
                mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeListActivity.this);

                builder.setMessage("Import or export... TBC");
                builder.setPositiveButton(getString(R.string.export_option), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        mGoogleApiClient.connect();

                        Toast.makeText(RecipeListActivity.this, "Shopping list generated successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(getString(R.string.import_option), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });

                builder.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private String exportRecipesToJSONString()
    {
        DatabaseManager manager = new DatabaseManager(this);

        manager.open();

        ArrayList<String> recipeNames = manager.getAllRecipeNames();

        if (recipeNames.size() > 0)
        {
            try
            {
                JSONObject outerJSONObject = new JSONObject();
                JSONArray recipesJSONArray = new JSONArray();

                for (String recipeName : recipeNames)
                {
                    ArrayList<ShoppingItem> shoppingItems = manager.getAllShoppingItemsForRecipe(recipeName);

                    for (ShoppingItem item : shoppingItems)
                    {
                        JSONObject itemJSONObject = new JSONObject();

                        itemJSONObject.put("item", item.getItem());
                        itemJSONObject.put("name", item.getItem());
                        itemJSONObject.put("quantity", item.getQuantity());
                        itemJSONObject.put("recipeName", item.getRecipeName());

                        recipesJSONArray.put(itemJSONObject);
                    }

                    outerJSONObject.put("recipes", recipesJSONArray);
                }

                String recipesJSONString = outerJSONObject.toString();

                //                File file = new File(path + "/Download");
                //
                //                if (!file.exists())
                //                    file.mkdir();
                //
                //                file = new File(path + "/Download/recipes_PMP.json");
                //
                //                FileOutputStream stream = new FileOutputStream(file);
                //                try
                //                {
                //                    stream.write(recipesJSONString.getBytes());
                //                }
                //                finally
                //                {
                //                    stream.close();
                //                }

                return recipesJSONString;
            }

            catch (Exception e)
            {
                e.printStackTrace();
                return "";
            }
        }
        else
            return "";
    }

    @Override
    protected void onResume()
    {
        DatabaseManager manager = new DatabaseManager(this);

        manager.open();

        ArrayList<String> recipeNamesList = manager.getAllRecipeNames();

        if (forCalendar)
            adapter = new RecipeListAdapter(this, true);
        else
            adapter = new RecipeListAdapter(this, false);

        adapter.setRecipeItemsList(recipeNamesList);

        recipeListView.setAdapter(adapter);

        manager.close();

        super.onResume();
    }

    private void deleteSelectedItems()
    {
        DatabaseManager manager = new DatabaseManager(this);
        manager.open();
        for (int i = 0; i < adapter.getCount(); i++)
        {
            if (recipeListView.isItemChecked(i))
            {
                String selectedItem = (String) adapter.getItem(i);
                manager.deleteRecipeItem(selectedItem);
            }
            adapter.notifyDataSetChanged();
        }
        manager.close();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, "Powerful Meal Planner recipes")).build();

        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(metadataCallback);

        //        DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, tempDriveID);
        //
        //        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(contentsOpenedCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new ResultCallback<DriveApi.MetadataBufferResult>()
    {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result)
        {
            if (!result.getStatus().isSuccess())
            {
                //                showMessage("Problem while retrieving results");
                return;
            }

            MetadataBuffer mbr = result.getMetadataBuffer();

            if (mbr.getCount() > 0)
            {
                Metadata metadata = mbr.get(0);
                DriveId driveIDofRecipeFile = metadata.getDriveId();

                Drive.DriveApi.getFile(mGoogleApiClient, driveIDofRecipeFile).open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(contentsOpenedCallback);
            }
            else
            {
                createNewRecipeFileOnGoogleDrive();
            }
        }
    };

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if (connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            }
            catch (IntentSender.SendIntentException e)
            {
                // Unable to resolve, message user appropriately
            }
        }
        else
        {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        switch (requestCode)
        {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new ResultCallback<DriveFolder.DriveFileResult>()
    {
        @Override
        public void onResult(DriveFolder.DriveFileResult result)
        {
            if (!result.getStatus().isSuccess())
            {
                Toast.makeText(RecipeListActivity.this, "Error while trying to create the file", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(RecipeListActivity.this, "Created a file with content: " + result.getDriveFile().getDriveId(), Toast.LENGTH_SHORT).show();
            tempDriveID = result.getDriveFile().getDriveId();
        }
    };

    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>()
    {
        @Override
        public void onResult(DriveApi.DriveContentsResult result)
        {
            if (!result.getStatus().isSuccess())
            {
                Log.e("Error:", "No se puede abrir el archivo o no se encuentra");
                return;
            }
            // DriveContents object contains pointers
            // to the actual byte stream
            DriveContents contents = result.getDriveContents();
            BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            String contentsAsString = builder.toString();
            Log.e("RESULT:", contentsAsString);
        }
    };

    private void createNewRecipeFileOnGoogleDrive()
    {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>()
        {
            @Override
            public void onResult(DriveContentsResult driveContentsResult)
            {
                if (!driveContentsResult.getStatus().isSuccess())
                {
                    Toast.makeText(RecipeListActivity.this, "Error while trying to create new file contents", Toast.LENGTH_SHORT).show();
                    return;
                }
                final DriveContents driveContents = driveContentsResult.getDriveContents();

                String recipesJSONString = exportRecipesToJSONString();

                if (recipesJSONString.length() > 0)
                {
                    OutputStream outputStream = driveContents.getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);
                    try
                    {
                        writer.write(recipesJSONString);
                        writer.close();
                    }
                    catch (IOException e)
                    {
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("Powerful Meal Planner recipes").setMimeType("text/plain").setStarred(false).build();

                    // create a file on root folder
                    Drive.DriveApi.getRootFolder(mGoogleApiClient).createFile(mGoogleApiClient, changeSet, driveContents).setResultCallback(fileCallback);
                }
                else
                {
                    Toast.makeText(RecipeListActivity.this, "Could not write JSON", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
