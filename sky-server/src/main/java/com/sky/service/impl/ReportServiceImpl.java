package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnOverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localDateTimeMIN = LocalDateTime.of(date, LocalTime.MIN);

            Map map = new HashMap();
            map.put("beginTime", localDateTimeMIN);
            map.put("endTime", localDateTimeMAX);
            map.put("status", Orders.COMPLETED);
            Double amount = orderMapper.sumAmount(map);
            turnoverList.add(amount == null ? 0.0 : amount);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> NewUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localDateTimeMIN = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("beginTime", localDateTimeMIN);
            map.put("endTime", localDateTimeMAX);
            Integer count = userMapper.sumUser(map);
            NewUserList.add(count == null ? 0 : count);
        }

        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            Integer count = userMapper.getTotalByDate(localDateTimeMAX);
            totalUserList.add(count == null ? 0 : count);
        }


        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(NewUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localDateTimeMIN = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("beginTime", localDateTimeMIN);
            map.put("endTime", localDateTimeMAX);
            Integer count = orderMapper.sumOrderNumber(map);
            orderCountList.add(count == null ? 0 : count);
        }
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localDateTimeMIN = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("beginTime", localDateTimeMIN);
            map.put("endTime", localDateTimeMAX);
            map.put("status", Orders.COMPLETED);
            Integer count = orderMapper.sumValidOrderNumber(map);
            validOrderCountList.add(count == null ? 0 : count);
        }
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(validOrderCount * 1.0 / totalOrderCount)
                .build();
    }

    /**
     * 销量top10排名
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime localDateTimeMAX = LocalDateTime.of(end, LocalTime.MAX);
        LocalDateTime localDateTimeMIN = LocalDateTime.of(begin, LocalTime.MIN);
        List<GoodsSalesDTO> goodSalesTop10 = orderMapper.getGoodSalesTop10(localDateTimeMIN, localDateTimeMAX);
        List<String> names = goodSalesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = goodSalesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }

    /**
     * 导出营业数据
     */
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库，获取营业数据
        LocalDate beginTime = LocalDate.now().minusDays(30);
        LocalDate endTime = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(beginTime, LocalTime.MIN), LocalDateTime.of(endTime, LocalTime.MAX));

        //通过POI将数据写入到Excel文件中
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Template/运营数据报表模板.xlsx");

        //基于模板文件创建一个新的excel文件
        try {
            //基于模板文件创建一个新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("时间范围：" + beginTime + "至" + endTime);
            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for(int i = 0 ; i < 30 ; i++) {
                LocalDate date = beginTime.plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData1.getTurnover());
                row.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData1.getUnitPrice());
                row.getCell(6).setCellValue(businessData1.getNewUsers());
            }
            //通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            excel.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}