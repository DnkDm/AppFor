package com.example.appfor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
    private List<PlacemarkMapObject> placemarkMapObjects;
    private MapObjectCollection mapObjectCollection;
    private LocationManager locationManager;
    private UserLocationLayer userLocationLayer;
    private CircleMapObject userCircle;

    private TextView scoreCounter;
    private TextView winTable;
    private int score = 0;
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
        scoreCounter = findViewById(R.id.scoreCounter);
        requestLocationPermission();
        addMarkers();


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


        mapObjectTapListener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast toast = Toast.makeText(getApplicationContext(),
                         "+++"  , Toast.LENGTH_SHORT);
                toast.show();
                if (placemarkMapObjects.get(idOfPlacemark).isVisible() == true){
                    score++;
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("STRING", stringList.get(idOfPlacemark));
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

        placemark1.setIconStyle(new IconStyle().setScale(0.3f));
        placemark2.setIconStyle(new IconStyle().setScale(0.3f));

        placemarkMapObjects.add(placemark1);
        placemarkMapObjects.add(placemark2);

        String text1 = "Dostopr 1";
        String text2 = "Dostopr 2";

        stringList.add(text1);
        stringList.add(text2);
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