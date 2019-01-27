package com.example.systemtaskapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.systemtaskapp.Database.UserRepository;
import com.example.systemtaskapp.Local.UserDataSource;
import com.example.systemtaskapp.Local.UserDatabase;
import com.example.systemtaskapp.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
  private ListView lstUser;
  private FloatingActionButton fab;

  //Adapter
    List<User> userList=new ArrayList<>();
    ArrayAdapter adapter;

    //Database
    private CompositeDisposable compositeDisposable;
    private UserRepository userRepository;
    private EditText name,mobile_num;
    private Button save,edit,delete,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        logout=(Button)findViewById(R.id.btn_logout);
        lstUser=(ListView)findViewById(R.id.lstUsers);

        //Init
        compositeDisposable=new CompositeDisposable();

        //Init View
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,userList);
        registerForContextMenu(lstUser);
        lstUser.setAdapter(adapter);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*lstUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View v, int arg2,
                                    long arg3) {
                v = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
                dialog.setContentView(v);
                dialog.show();
                name=(EditText)dialog.findViewById(R.id.bottom_name);
                mobile_num=(EditText)dialog.findViewById(R.id.bottom_mobile);
                edit=(Button)dialog.findViewById(R.id.bottom_edit);
                delete=(Button)dialog.findViewById(R.id.bottom_delete);
                save=(Button)dialog.findViewById(R.id.bottom_save);
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                String text=(String)lstUser.getItemAtPosition(arg2).toString();
                name.setText(text.replaceAll("\\d",""));
                mobile_num.setText(text.replaceAll("[^\\d.]", ""));
                name.setEnabled(false);
                mobile_num.setEnabled(false);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name.setEnabled(true);
                        mobile_num.setEnabled(true);

                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateUser(user);
                        Toast.makeText(MainActivity.this,"Delete",Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });*/

        //DataBase
        UserDatabase userDatabase=UserDatabase.getInstance(this); //create Database
        userRepository=UserRepository.getInstance(UserDataSource.getInstance(userDatabase.userDAO()));

        //load all data from database
        loadData();

         //Event
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
                final BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
                dialog.setContentView(view);
                dialog.show();
                name=(EditText)dialog.findViewById(R.id.bottom_name);
                mobile_num=(EditText)dialog.findViewById(R.id.bottom_mobile);
                save=(Button)dialog.findViewById(R.id.bottom_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //Adding Data from bottom sheet
                        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                                User user=new User(name.getText().toString(), mobile_num.getText().toString());
                                userRepository.insertUser(user);
                                e.onComplete();
                            }
                        })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer() {
                                               @Override
                                               public void accept(Object o) throws Exception {
                                                   Toast.makeText(MainActivity.this,"User Added!",Toast.LENGTH_SHORT).show();
                                               }
                                           }, new Consumer<Throwable>() {
                                               @Override
                                               public void accept(Throwable throwable) throws Exception {
                                                   Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                               }
                                           }, new Action() {
                                               @Override
                                               public void run() throws Exception {
                                                   loadData();   //Refresh data
                                               }
                                           }
                                );
                    }
                });
            }
        });





    }

    private void loadData() {
            //Using Rx Java

        Disposable disposable=userRepository.getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io() )
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        onGetAllUserSucess(users);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

             compositeDisposable.add(disposable);



    }

    private void onGetAllUserSucess(List<User> users) {
     userList.clear();
     userList.addAll(users);
     adapter.notifyDataSetChanged();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
           case R.id.menu_clear:
               deleteAllUsers();
               break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Select Action:");

        menu.add(Menu.NONE,0,Menu.NONE,"UPDATE");
        menu.add(Menu.NONE,1,Menu.NONE,"DELETE");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final User user=userList.get(info.position);
        switch (item.getItemId()){

            case 0: //update
            {
                final EditText edtName=new EditText(MainActivity.this);
                edtName.setText(user.getName());
                edtName.setHint("Enter your Name");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edit")
                        .setMessage("Edit name")
                        .setView(edtName)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             if(TextUtils.isEmpty(edtName.getText().toString()))
                             {
                                 return;
                             }
                                else{
                                    user.setName(edtName.getText().toString());
                                   updateUser(user);
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
            case 1:   //Delete
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to delete"+user.toString())
                        //.setView(edtMobile)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              deleteUser(user);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
            break;
        }
       return true;
    }

    private void deleteUser(final User user) {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                userRepository.deleteUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {
                                   Toast.makeText(MainActivity.this,"User Added!",Toast.LENGTH_SHORT).show();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   loadData();   //Refresh data
                               }
                           }
                );

        compositeDisposable.add(disposable);


    }

    private void deleteAllUsers() {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                userRepository.deleteAllUsers();
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   loadData();   //Refresh data
                               }
                           }
                );
        compositeDisposable.add(disposable);


    }

    private void updateUser(final User user){
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                userRepository.updateUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {
                                   Toast.makeText(MainActivity.this,"User Added!",Toast.LENGTH_SHORT).show();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   loadData();   //Refresh data
                               }
                           }
                );

        compositeDisposable.add(disposable);

    }



}
