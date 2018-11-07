package s.s.rxjavaexample;

import com.hwangjr.rxbus.Bus;

/**
 * Created by ChenLei on 2018/11/6 0006.
 */
public class RxBus {

    private static Bus sBus;

    public static synchronized  Bus get(){
        if(sBus == null){
            sBus = new Bus();
        }
        return sBus;
    }

    public void post(){

    }
}
