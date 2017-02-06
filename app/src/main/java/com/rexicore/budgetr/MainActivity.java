package com.rexicore.budgetr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewExpensesWindow.NewExpensesDialogListener,
        NewGroupWindow.NewGroupWindowListener, MonthlyPlansFrag.MonthlyPlansOnClickListener,
        NewMonthExpenseWindow.NewMonthExpenseListener{

    private int navigationDrawerIndex = 0;

    private long selGroupToAdd = -1;
    private FragmentManager fragmentManager;
    private TotalFeesFrag totalFeesFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (navigationDrawerIndex) {
                    case 0:
                        buildNewExpensesWindow();
                        break;
                    case 1:
                        MonthlyPlansFrag frag = (MonthlyPlansFrag) fragmentManager.findFragmentByTag("MonthlyPlansFrag");
                        if(frag != null && frag.isVisible()) {
                            buildNewGroupWindow();
                        } else {
                            buildNewMonthExpenseWindow();
                        }
                        break;
                    default:
                        break;
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        totalFeesFrag = new TotalFeesFrag();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, totalFeesFrag, "TotalFeesFrag").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFragUI() {
        switch (navigationDrawerIndex) {
            case 0:
                TotalFeesFrag frag0 = (TotalFeesFrag) fragmentManager.findFragmentByTag("TotalFeesFrag");
                frag0.updateUI();
                break;
            case 1:
                Fragment fragment = fragmentManager.findFragmentByTag("MonthlyPlansFrag");
                if (fragment != null && fragment.isVisible()) {
                    MonthlyPlansFrag frag1 = (MonthlyPlansFrag) fragmentManager.findFragmentByTag("MonthlyPlansFrag");
                    frag1.updateUI();
                } else {
                    MonthlyFeesFrag frag1 = (MonthlyFeesFrag) fragmentManager.findFragmentByTag("MonthlyFeesFrag");
                    frag1.updateUI();
                }
                break;
            default:
                break;
        }
    }

    private void buildNewExpensesWindow() {
        NewExpensesWindow dialog = new NewExpensesWindow();
        dialog.show(getSupportFragmentManager(), "new_expenses");
    }

    private void buildNewGroupWindow() {
        NewGroupWindow dialog = new NewGroupWindow();
        dialog.show(getSupportFragmentManager(), "new_group");
    }

    private void buildNewMonthExpenseWindow() {
        NewMonthExpenseWindow dialog = new NewMonthExpenseWindow();
        dialog.show(fragmentManager, "new_monthly_expense");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Get views
        EditText expenseTitleET = (EditText) dialog.getDialog().findViewById(R.id.new_expense_title_et);
        EditText expenseAmountET = (EditText) dialog.getDialog().findViewById(R.id.new_expense_amount_et);
        // Get expense info
        String title = expenseTitleET.getText().toString();
        double amount = Double.parseDouble(expenseAmountET.getText().toString());
        // Add to db
        DbHandler db = new DbHandler(this);
        Expense expense = new Expense(-1, title, amount, "gen");
        db.newFee(expense);
        updateFragUI();
        db.close();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    @Override
    public void onNewGroupDialogPositiveClick(DialogFragment dialog) {
        // Get view
        EditText newGroupName = (EditText) dialog.getDialog().findViewById(R.id.new_group_name_edittext);
        // Get group name
        String group = newGroupName.getText().toString();
        // Add to db
        DbHandler db = new DbHandler(this);
        db.newGroup(group);
        updateFragUI();
        db.close();
    }

    @Override
    public void onNewGroupDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    @Override
    public void onNewMonthExpensePositiveClick(DialogFragment dialog) {
        EditText expenseTitle = (EditText) dialog.getDialog().findViewById(R.id.new_month_expense_edittext);
        EditText expenseMonthlyAmt = (EditText) dialog.getDialog().findViewById(R.id.new_month_expense_monthly_edittext);
        EditText expenseYearlyAmt = (EditText) dialog.getDialog().findViewById(R.id.new_month_expense_yearly_edittext);
        // Acquire data
        String moExpName = expenseTitle.getText().toString();

        double monthlyAmt = 0;
        if (expenseMonthlyAmt.getText().length() > 0) {
            // Monthly data entered
            monthlyAmt = Double.parseDouble(expenseMonthlyAmt.getText().toString());
        } else if (expenseYearlyAmt.getText().length() > 0) {
            // No monthly data entered
            monthlyAmt = Double.parseDouble(expenseYearlyAmt.getText().toString()) / 12.0f;
        }

        // Create expense for specific group and add to db
        Expense monthExpense = new Expense(-1, moExpName, monthlyAmt, selGroupToAdd, "monthly");
        DbHandler db = new DbHandler(this);
        db.newFee(monthExpense);
        db.close();

        // Update UI
        updateFragUI();
    }

    @Override
    public void onNewMonthExpenseNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        String tag = "";

        if (id == R.id.main_page) {
            // Set main fragment here
            navigationDrawerIndex = 0;
            tag = "TotalFeesFrag";
        } else if (id == R.id.monthly_page) {
            navigationDrawerIndex = 1;
            tag = "MonthlyPlansFrag";
        }

        fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (tag.equals("TotalFeesFrag")) {
                fragment = new TotalFeesFrag();
            } else if (tag.equals("MonthlyPlansFrag")) {
                fragment = new MonthlyPlansFrag();
            }
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, tag).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMonthlyPlanClick(long selectedId) {
        MonthlyFeesFrag fragment = MonthlyFeesFrag.newInstance(selectedId);
        selGroupToAdd = selectedId;

        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment, "MonthlyFeesFrag")
                .commit();
    }
}
