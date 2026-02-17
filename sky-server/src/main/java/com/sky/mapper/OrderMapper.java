package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void OrderSubmit(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 查询历史订单信息
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);



    @Select("select * from orders where id = #{id}")
    OrderVO getById(Long id);


    @Select("select count(*) from orders where status = #{comfiemed}")
    Integer countStatus(Integer confirmed);


    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);




    /**
     * 统计订单总金额
     * @param map
     * @return
     */
    Double sumAmount(Map map);

    /**
     * 统计订单数量
     * @param map
     * @return
     */
    Integer sumOrderNumber(Map map);
    /**
     * 统计有效订单数量
     * @param map
     * @return
     */
    Integer sumValidOrderNumber(Map map);
    /**
     * 获取销量排名top10
      * @param beginTime
      * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getGoodSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取订单状态数据
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
