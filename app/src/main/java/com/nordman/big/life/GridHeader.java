package com.nordman.big.life;

/**
 * Created by s_vershinin on 20.05.2016.
 */
public class GridHeader {
    public int id;
    public String name;

    public GridHeader(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
