package com.example.yumashish.gogamarkethuddle;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class GHUnitTest {
    DatabaseConnector DBC;

    public static Printer DebugPrinter() {
        return new Printer() {
            @Override
            public void Print(String content) {
                System.out.println(content);
            }
        };
    }

    @Test
    public void DatabaseInit() {
        try {
            DBC = new DatabaseConnector();
            LoginResponseJson response = DBC.GetAuthSecretKey("yumashish@gmail.com", "yumashish", -1);
            if(response.result_code.equals("verified_result")) {
                DBC.SetAccessDetails(response.result);
                assertTrue(true);
            } else {
                DBC = null;
                DBC.FlushDebugQueue(DebugPrinter());
                fail();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            DBC = null;
            DBC.FlushDebugQueue(DebugPrinter());
            fail();
        }
    }

    @Test
    public void GenerateAccessTokenTest() throws Exception {
        if (DBC == null) DatabaseInit();
        assertTrue(DBC != null);

        QueryPairs queryPairs = new QueryPairs();
        queryPairs.Add("query_type", "user_by_id");
        queryPairs.Add("user_id", "1");

        DBC.AuthSign(queryPairs);

        assertTrue(queryPairs.GetValue("access_key") != null);
    }

    @Test
    public void GetAllUsersTest() throws Exception {
        if (DBC == null) DatabaseInit();
        assertTrue(DBC != null);

        try {
            UserListJson response = DBC.GetUsersInsideArea(new LatLng(0, 0), 10000d);
            if(response.result_code.equals("verified_result")) {
                System.out.println("Get All Users: " + response.result.size());
                assertFalse(response.result.size() == 0);
            } else {
                DBC.FlushDebugQueue(DebugPrinter());
                System.out.println(response.result_code);
                if(response.result != null)
                    System.out.println(response.result);
                fail();
            }
        } catch (IllegalArgumentException iarg) {
            iarg.printStackTrace();
            DBC.FlushDebugQueue(DebugPrinter());
            fail();
        } catch (JsonParseException iarg) {
            iarg.printStackTrace();
            DBC.FlushDebugQueue(DebugPrinter());
            fail();
        }
    }

    @Test
    public void GetAllSellersTest() throws Exception {
        if (DBC == null) DatabaseInit();
        assertTrue(DBC != null);

        try {
            SellerListJson response = DBC.GetSellersInsideArea(new LatLng(0, 0), 10000d);
            if(response.result_code.equals("verified_result")) {
                System.out.println("Get All Sellers: " + response.result.size());
                assertFalse(response.result.size() == 0);
            } else {
                DBC.FlushDebugQueue(DebugPrinter());
                System.out.println(response.result_code);
                if(response.result != null)
                    System.out.println(response.result);
                fail();
            }
        } catch (IllegalArgumentException iarg) {
            iarg.printStackTrace();
            DBC.FlushDebugQueue(DebugPrinter());
            fail();
        } catch (JsonParseException iarg) {
            iarg.printStackTrace();
            DBC.FlushDebugQueue(DebugPrinter());
            fail();
        }
    }


    public void SendBroadcastMessageTest() {
        if(DBC == null) DatabaseInit();
        final int _currentUserId = 1;
        final String _content = "Test Broadcast @ " + System.currentTimeMillis();
        final LatLng _location = new LatLng(135, 87);
        final double _radius = 500d;
        try {
            HttpResponse response = DBC.SendBroadcastMessage(_currentUserId, _content, _location, _radius);
            String responseText = DatabaseConnector.ResponseToText(response);
            System.out.println(responseText);
            assertTrue(responseText.length() == 0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail();
        }
    }

    public void GetMessagesTest() {

    }
}