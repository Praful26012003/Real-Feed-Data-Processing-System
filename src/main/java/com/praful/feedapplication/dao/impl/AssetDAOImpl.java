package com.praful.feedapplication.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.praful.feedapplication.constants.CommonConstants;
import com.praful.feedapplication.controller.AssetController;
import com.praful.feedapplication.dao.AssetDAO;
import com.praful.feedapplication.dao.SQLHandler;
import com.praful.feedapplication.protos.PageRequestForSpotPriceListEntity;
import com.praful.feedapplication.protos.SpotPriceRequestEntity;
import com.praful.feedapplication.protos.SpotPriceResponseEntity;
import com.praful.feedapplication.protos.SpotPricesListResponseEntity;
import com.praful.feedapplication.protos.SpotPricesRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AssetDAOImpl implements AssetDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetController.class);
    private final JdbcTemplate jdbcTemplate;

    public AssetDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public int addAssetSpotPrice(SpotPriceRequestEntity spotPricePriceRequest) {
        int rowsUpdated = 0;
        rowsUpdated = jdbcTemplate.update(SQLHandler.INSERT_ASSET_SPOT_PRICE, spotPricePriceRequest.getDate(), CommonConstants.METAL, CommonConstants.CURRENCY, CommonConstants.WEIGHT_UNIT, spotPricePriceRequest.getAsk(), spotPricePriceRequest.getMid(),
                spotPricePriceRequest.getBid(), spotPricePriceRequest.getValue(), spotPricePriceRequest.getPerformance());

        if (rowsUpdated == 0) {
            LOGGER.warn("Error adding data to the database");
        } else {
            LOGGER.info("spot price data has been added to the database");
        }
        return rowsUpdated;
    }
    @Override
    public int addAssetLastDayPrice(SpotPricesRequestEntity spotPrices) {
        int rowsUpdated = 0;
        rowsUpdated = jdbcTemplate.update(SQLHandler.INSERT_LAST_DAY_ASSET_DETAIL, spotPrices.getEmbedded().getLastHistoricalSpotPrice().getDate(), CommonConstants.METAL, CommonConstants.CURRENCY, CommonConstants.WEIGHT_UNIT,
                spotPrices.getEmbedded().getLastHistoricalSpotPrice().getClose(),
                spotPrices.getEmbedded().getLastHistoricalSpotPrice().getHigh(),
                spotPrices.getEmbedded().getLastHistoricalSpotPrice().getLow(),
                spotPrices.getEmbedded().getLastHistoricalSpotPrice().getOpen());
        if (rowsUpdated == 0) {
            LOGGER.warn("Error adding data to the database");
        } else {
            LOGGER.info("spot price data has been added to the database");
        }
        return rowsUpdated;
    }

    @Override
    public SpotPriceResponseEntity fetchAssetSpotPrice() {

        RowMapper<SpotPriceResponseEntity> mapper = new RowMapper<SpotPriceResponseEntity>() {
            @Override
            public SpotPriceResponseEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                SpotPriceResponseEntity spotPrice = SpotPriceResponseEntity.newBuilder()
                        .setDate(String.valueOf(rs.getTimestamp("date")))
                        .setWeightUnit(rs.getString("weight_unit"))
                        .setAsk(rs.getDouble("ask"))
                        .setMid(rs.getDouble("mid"))
                        .setBid(rs.getDouble("bid"))
                        .setValue(rs.getDouble("value"))
                        .setPerformance(rs.getDouble("performance"))
                        .build();

                return spotPrice;
            }
        };

        SpotPriceResponseEntity spotPrice = jdbcTemplate.queryForObject(SQLHandler.FETCH_ASSET_SPOT_PRICE, mapper);
        return spotPrice;
    }

    @Override
    public SpotPricesListResponseEntity fetchBetweenTimeStampAssetSpotPrices(PageRequestForSpotPriceListEntity pageRequestForSpotPriceList) {

        List<Map<String, Object>> goldSpotPriceList = jdbcTemplate.queryForList(SQLHandler.FETCH_BETWEEN_TIMESTAMP_PAGINATED_ASSET_SPOT_PRICES, pageRequestForSpotPriceList.getFromTime(), pageRequestForSpotPriceList.getToTime(), pageRequestForSpotPriceList.getPageSize());

        List<SpotPriceResponseEntity> betweenTimeStampGoldSpotPriceList = new ArrayList<>();

        goldSpotPriceList.forEach(goldSpotPrice -> {

            SpotPriceResponseEntity.Builder spotPrice = SpotPriceResponseEntity.newBuilder()
                    .setAsk((Double) goldSpotPrice.get("ask"))
                    .setBid((Double) goldSpotPrice.get("bid"))
                    .setMid((Double) goldSpotPrice.get("mid"))
                    .setValue((Double) goldSpotPrice.get("value"))
                    .setPerformance((Double) goldSpotPrice.get("performance"))
                    .setDate((String.valueOf(goldSpotPrice.get("date"))));

            betweenTimeStampGoldSpotPriceList.add(spotPrice.build());
        });

        return SpotPricesListResponseEntity.newBuilder().addAllSpotPrices(betweenTimeStampGoldSpotPriceList).build();
    }

    @Override
    public SpotPricesListResponseEntity fetchForwardPaginatedAssetSpotPrices(PageRequestForSpotPriceListEntity pageRequestForSpotPriceList) {

        List<Map<String, Object>> goldSpotPriceList = jdbcTemplate.queryForList(SQLHandler.FETCH_FORWARD_PAGINATED_ASSET_SPOT_PRICES, pageRequestForSpotPriceList.getFromTime(), pageRequestForSpotPriceList.getToTime(), pageRequestForSpotPriceList.getPageSize());
        List<SpotPriceResponseEntity> paginatedGoldSpotPriceList = new ArrayList<>();
        goldSpotPriceList.forEach(goldSpotPrice -> {
            SpotPriceResponseEntity.Builder spotPrice = SpotPriceResponseEntity.newBuilder()
                    .setAsk((Double) goldSpotPrice.get("ask"))
                    .setBid((Double) goldSpotPrice.get("bid"))
                    .setMid((Double) goldSpotPrice.get("mid"))
                    .setValue((Double) goldSpotPrice.get("value"))
                    .setPerformance((Double) goldSpotPrice.get("performance"))
                    .setDate((String.valueOf(goldSpotPrice.get("date"))));

            paginatedGoldSpotPriceList.add(spotPrice.build());
        });
        return SpotPricesListResponseEntity.newBuilder().addAllSpotPrices(paginatedGoldSpotPriceList).build();
    }

    @Override
    public SpotPricesListResponseEntity fetchReversePaginatedAssetSpotPrices(PageRequestForSpotPriceListEntity pageRequestForSpotPriceList) {
        List<Map<String, Object>> goldSpotPriceList = jdbcTemplate.queryForList(SQLHandler.FETCH_REVERSE_PAGINATED_ASSET_SPOT_PRICES, pageRequestForSpotPriceList.getFromTime(), pageRequestForSpotPriceList.getToTime(), pageRequestForSpotPriceList.getPageSize());
        List<SpotPriceResponseEntity> reversePaginatedGoldSpotPriceList = new ArrayList<>();
        goldSpotPriceList.forEach(goldSpotPrice -> {
            SpotPriceResponseEntity.Builder spotPrice = SpotPriceResponseEntity.newBuilder()
                    .setAsk((Double) goldSpotPrice.get("ask"))
                    .setBid((Double) goldSpotPrice.get("bid"))
                    .setMid((Double) goldSpotPrice.get("mid"))
                    .setValue((Double) goldSpotPrice.get("value"))
                    .setPerformance((Double) goldSpotPrice.get("performance"))
                    .setDate((String.valueOf(goldSpotPrice.get("date"))));
            reversePaginatedGoldSpotPriceList.add(spotPrice.build());
        });
        return SpotPricesListResponseEntity.newBuilder().addAllSpotPrices(reversePaginatedGoldSpotPriceList).build();
    }
}
