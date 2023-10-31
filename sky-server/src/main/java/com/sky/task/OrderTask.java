package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Resource
    private OrderMapper orderMapper;

    /**
     * 处理支付超时
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder(){
        log.info("定时处理超时订单:{}", LocalDateTime.now());
        //select * from orders where status = ? and order_time < (当前时间 -15min)
        LocalDateTime beforeTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> pendingOrder = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, beforeTime);

        if(pendingOrder != null && !pendingOrder.isEmpty()) {
            for (Orders order : pendingOrder) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时,自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    /**
     * 01:00处理配送超时
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理处于派送中的订单: {}",LocalDateTime.now());

        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now());

        if(orderList!=null&&!orderList.isEmpty()){
            for (Orders orders : orderList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
