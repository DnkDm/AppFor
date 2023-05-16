package com.example.appfor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {

    private MapView mapView;
    private static List<PlacemarkMapObject> placemarkMapObjects;
    private MapObjectCollection mapObjectCollection;
    private LocationManager locationManager;
    private UserLocationLayer userLocationLayer;
    private CircleMapObject userCircle;

    private List<Integer> imageViewList = new ArrayList<>();
    private List<String> stringOp = new ArrayList<>();

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPreferences2;
    public static SharedPreferences.Editor editor2;

    private static TextView scoreCounter;
    private static TextView winTable;
    private Button buttonMenu;

    public static int score = 0;
    public static String kod = "00000";

    private FusedLocationProviderClient fusedLocationClient;
    MapObjectTapListener mapObjectTapListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("119874c7-2cae-4d87-8264-98bec3a19540");

        MapKitFactory.initialize(this);

        // Укажите имя Activity вместо map.
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(56.837648, 60.599684), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        placemarkMapObjects = new ArrayList<>();
        showDiolig();

        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setObjectListener(this);
        userLocationLayer.setAutoZoomEnabled(false);
        userLocationLayer.setHeadingEnabled(false);


        winTable = findViewById(R.id.winTable);
        winTable.setTextColor(Color.TRANSPARENT);

        buttonMenu = findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent1);
            }
        });
        scoreCounter = findViewById(R.id.scoreCounter);
        requestLocationPermission();
        //nen


        sharedPreferences = this.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferences2 = this.getSharedPreferences("mySharedPreferences2", Context.MODE_PRIVATE);
        editor2 = sharedPreferences2.edit();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);  // Устанавливаем интервал обновления в 1000 мс (1 секунда)
        locationRequest.setFastestInterval(500);  // Максимально быстрый интервал - 500 мс
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Обновляем местоположение в MapKit
                    userLocationLayer.resetAnchor();
                    /*userLocationLayer.setAnchor(
                            new PointF((float) (0.5 * mapView.getWidth()), (float) (0.5 * mapView.getHeight())),
                            new PointF((float) (0.5 * mapView.getWidth()), (float) (0.83 * mapView.getHeight())));*/
                }
            }
        }, null);

        kod = sharedPreferences2.getString("myString", "00000");

        addMarkers();//nen


        score = sharedPreferences.getInt("myInt", 0);
        scoreCounter.setText("Score " + score + "/2");

        /*ditor2.clear();
        editor2.apply();
        editor.clear();
        editor.apply();*/


        mapObjectTapListener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast toast = Toast.makeText(getApplicationContext(),
                         "+++"  , Toast.LENGTH_SHORT);
                toast.show();
                if (placemarkMapObjects.get(idOfPlacemark).isVisible() == true){
                    score++;

                    editor.putInt("myInt", score);
                    editor.apply();

                    char[] myNameChars = kod.toCharArray();
                    myNameChars[idOfPlacemark] = '1';
                    kod = String.valueOf(myNameChars);
                    editor2.putString("myString", kod);
                    editor2.apply();
                    Log.d("had", kod);

                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("STRING", stringList.get(idOfPlacemark));
                    intent.putExtra("STRING2", stringOp.get(idOfPlacemark));
                    intent.putExtra("IMAGE", imageViewList.get(idOfPlacemark));
                    startActivity(intent);
                }
                placemarkMapObjects.get(idOfPlacemark).setVisible(false);
                scoreCounter.setText("Score " + score + "/2");
                if (score == 2)
                    winTable.setTextColor(Color.BLACK);;
                return false;
            }
        };
    }

    public static void rebutMap(int score, String kod){
        winTable.setTextColor(Color.TRANSPARENT);

        editor.putInt("myInt", score);
        editor.apply();
        score = sharedPreferences.getInt("myInt", 0);
        scoreCounter.setText("Score " + score + "/2");

        editor2.putString("myString", kod);
        editor2.apply();
        kod = sharedPreferences2.getString("myString", "00000");
        Log.d("kodf",kod+"");
        for (PlacemarkMapObject placemark : placemarkMapObjects) {
            if (kod.charAt(placemarkMapObjects.indexOf(placemark)) == '0'){
                placemark.setVisible(true);
            } else {
                placemark.setVisible(false);
            }
        }
    }

    int idOfPlacemark;
    private void checkMarkersInCircle(Point userLocation) {
        for (PlacemarkMapObject placemark : placemarkMapObjects) {
            double d = Math.sqrt(Math.pow((userLocation.getLatitude()-placemark.getGeometry().getLatitude()),2)+Math.pow((userLocation.getLongitude()-placemark.getGeometry().getLongitude()),2));
            //Log.d("heh", "-- " + d);
            if (d < 0.00185) {
                placemark.setIcon(ImageProvider.fromResource(this, R.drawable.mynew));
                placemark.setIconStyle(new IconStyle().setScale(0.5f));
                idOfPlacemark = placemarkMapObjects.indexOf(placemark);
                placemark.addTapListener(mapObjectTapListener);
            } else {
                placemark.setIcon(ImageProvider.fromResource(this, R.drawable.mynewred));
                placemark.removeTapListener(mapObjectTapListener);
                placemark.setIconStyle(new IconStyle().setScale(0.3f));
            }
        }
    }

    private List<String> stringList = new ArrayList<>();

    private void addMarkers(){
        mapObjectCollection = mapView.getMap().getMapObjects();
        PlacemarkMapObject placemark1 = mapObjectCollection.addPlacemark(new Point(56.840614, 60.616116), ImageProvider.fromResource(this, R.drawable.mynewred));
        PlacemarkMapObject placemark2 = mapObjectCollection.addPlacemark(new Point(56.841854, 60.593978), ImageProvider.fromResource(this, R.drawable.mynewred));
        PlacemarkMapObject MyzeiArchIDis = mapObjectCollection.addPlacemark(new Point(56.8358, 60.6064), ImageProvider.fromResource(this, R.drawable.mynewred));
        PlacemarkMapObject Yrgey = mapObjectCollection.addPlacemark(new Point(56.825754, 60.602730), ImageProvider.fromResource(this, R.drawable.mynewred));
        PlacemarkMapObject DvorecSporta = mapObjectCollection.addPlacemark(new Point(56.8194, 60.6408), ImageProvider.fromResource(this, R.drawable.mynewred));


        placemark1.setIconStyle(new IconStyle().setScale(0.3f));
        placemark2.setIconStyle(new IconStyle().setScale(0.3f));
        MyzeiArchIDis.setIconStyle(new IconStyle().setScale(0.3f));
        Yrgey.setIconStyle(new IconStyle().setScale(0.3f));
        DvorecSporta.setIconStyle(new IconStyle().setScale(0.3f));

        placemarkMapObjects.add(placemark1);
        placemarkMapObjects.add(placemark2);
        placemarkMapObjects.add(MyzeiArchIDis);
        placemarkMapObjects.add(Yrgey);
        placemarkMapObjects.add(DvorecSporta);

        Log.d("had", kod);
        for (PlacemarkMapObject placemark : placemarkMapObjects) {
            if (kod.charAt(placemarkMapObjects.indexOf(placemark)) == '0'){

            } else {
                placemark.setVisible(false);
            }
        }

        String text1 = "УРФУ";
        String text2 = "Правительство области";
        String text3 = "Музей архитектуры и дизайна";
        String text4 = "УРГЭУ";
        String text5 = "";
        String text6 = "";
        String text7 = "";
        String text8 = "";
        String text9 = "";
        String text10 = "";

        String textOp1 = "Уральский федеральный университет является крупнейшим вузом Урала, ведущим научно-образовательным центром региона и одним из крупнейших вузов Российской Федерации. В нём обучаются около 35 000[3] студентов, в том числе около 32 000 студентов очной формы обучения (по этому показателю УрФУ сопоставим только с МГУ, СПбГУ и ЮФУ).";
        String textOp2 = "Правительство Свердловской области — высший орган исполнительной власти Свердловской области. Руководство деятельностью правительства осуществляет высшее должностное лицо Свердловской области — губернатор Свердловской области.";
        String textOp3 = "Музей архитектуры и дизайна УрГАХУ — расположен в Историческом сквере Екатеринбурга (на Плотинке)\n 18 ноября 1973 года в День 250-летия Свердловска состоялось торжественное открытие Исторического сквера. Среди создателей этого уникального комплекса были ректор Свердловского архитектурного института (САИ) Н. С. Алфёров и его ведущие преподаватели В. А. Пискунов, Г. И. Дубровин, А. Э. Коротковский, А. В. Овечкин, а также архитектор института «Свердловскгражданпроект» Л. П. Винокурова.";
        String textOp4 = "Уральский государственный экономический университет (УрГЭУ) — федеральное государственное бюджетное образовательное учреждение высшего образования, расположенное в городе Екатеринбурге, которое готовит экономистов различного профиля, технологов, юристов и специалистов в области государственного и муниципального управления.";
        String textOp5 = "";
        String textOp6 = "";
        String textOp7 = "";
        String textOp8 = "";
        String textOp9 = "";
        String textOp10 = "";




        stringList.add(text1);
        stringList.add(text2);
        stringList.add(text3);
        stringList.add(text4);

        stringOp.add(textOp1);
        stringOp.add(textOp2);
        stringOp.add(textOp3);
        stringOp.add(textOp4);


        imageViewList.add(R.drawable.urfu);
        imageViewList.add(R.drawable.buildsome);
        imageViewList.add(R.drawable.chorto);
        imageViewList.add(R.drawable.yrt1);


    }

    private void requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        /*userLocationLayer.setAnchor(
                new PointF((float) (0.5 * mapView.getWidth()), (float) (0.5 * mapView.getHeight())),
                new PointF((float) (0.5 * mapView.getWidth()), (float) (0.83 * mapView.getHeight())));*/
        Point location = userLocationView.getPin().getGeometry();
        userCircle = mapView.getMap().getMapObjects().addCircle(new Circle(new Point(location.getLatitude(), location.getLongitude()), 150.0f), Color.BLUE, 2, Color.BLUE);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        Point location = userLocationView.getPin().getGeometry();
        Toast toast = Toast.makeText(getApplicationContext(),
                location.getLongitude() + " " + location.getLatitude() , Toast.LENGTH_SHORT);
        toast.show();

        checkMarkersInCircle(location);

        userCircle.setGeometry(new Circle(new Point(location.getLatitude(), location.getLongitude()), 150.0f));
        //userCircle = mapView.getMap().getMapObjects().addCircle(new Circle(new Point(location.getLatitude(), location.getLongitude()), 150.0f), Color.BLUE, 2, Color.BLUE);
    }

    public void showDiolig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Устанавливаем заголовок и сообщение
        builder.setTitle("Приветствие");
        builder.setMessage("Добро пожаловать в мое приложение! \n Твоя задача пройтись по всем достопримечательностям города Екатеринбрг, которые помечены красным кристаликом. Когда кристалик будет находится от тевя на расстоянии 150 метров, то есть находиться в твоем синем кругу, тебе надо будет НАЖАТЬ на него и тогда ты его соберешь. \n Нужно собрать все кристалы, удачи!!!");

        // Устанавливаем кнопку "ОК"
        builder.setPositiveButton("Понял, поехали!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Создаем и отображаем AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}