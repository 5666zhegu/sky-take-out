package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.sky.entity.Orders.*;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrders(){
        log.info("开始处理超时订单:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(PENDING_PAYMENT, time);

        if(orders != null && orders.size() > 0){
            for (Orders order : orders) {
                order.setStatus(CANCELLED);
                order.setCancelReason("支付超时，取消订单");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);

            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processCompleteOrdes(){
        log.info("开始处理派送订单:{}",LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(DELIVERY_IN_PROGRESS, time);

        if(orders != null && orders.size() > 0){
            for(Orders order : orders){
                order.setStatus(COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
