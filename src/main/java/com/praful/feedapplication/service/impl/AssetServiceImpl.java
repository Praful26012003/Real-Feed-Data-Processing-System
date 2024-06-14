package com.praful.feedapplication.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.praful.feedapplication.dao.AssetDAO;
import com.praful.feedapplication.exception.InternalServerErrorException;
import com.praful.feedapplication.exception.InvalidInputException;
import com.praful.feedapplication.protos.PageRequestDTO;
import com.praful.feedapplication.protos.PageRequestForSpotPriceListEntity;
import com.praful.feedapplication.protos.SpotPriceByPaginationDTO;
import com.praful.feedapplication.protos.SpotPriceRequestDTO;
import com.praful.feedapplication.protos.SpotPriceRequestEntity;
import com.praful.feedapplication.protos.SpotPriceResponseDTO;
import com.praful.feedapplication.protos.SpotPriceResponseEntity;
import com.praful.feedapplication.protos.SpotPricesListResponseEntity;
import com.praful.feedapplication.protos.SpotPricesRequestEntity;
import com.praful.feedapplication.service.AssetService;
import com.praful.feedapplication.utils.EncodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AssetServiceImpl implements AssetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetServiceImpl.class);
    private final AssetDAO assetDAO;

    public AssetServiceImpl(AssetDAO assetDAO) {
        this.assetDAO = assetDAO;
    }

    @Override
    public int addAssetSpotPrice(SpotPriceRequestDTO spotPriceRequest) {

        SpotPriceRequestEntity spotPrice = SpotPriceRequestEntity.newBuilder()
                .setDate(spotPriceRequest.getDate())
                .setWeightUnit(spotPriceRequest.getWeightUnit())
                .setAsk(spotPriceRequest.getAsk())
                .setBid(spotPriceRequest.getBid())
                .setMid(spotPriceRequest.getMid())
                .setValue(spotPriceRequest.getValue())
                .setPerformance(spotPriceRequest.getPerformance()).build();


        return assetDAO.addAssetSpotPrice(spotPrice);
    }

    @Override
    public int addAssetHistoricalSpotPrice(SpotPricesRequestEntity spotPrices) {
        return assetDAO.addAssetLastDayPrice(spotPrices);
    }

    @Override
    public SpotPriceResponseDTO fetchAssetSpotPrice() {

        try {
            SpotPriceResponseEntity spotPriceResponseEntity = assetDAO.fetchAssetSpotPrice();
            return SpotPriceResponseDTO.newBuilder().setDate(spotPriceResponseEntity.getDate())
                    .setWeightUnit(spotPriceResponseEntity.getWeightUnit())
                    .setAsk(spotPriceResponseEntity.getAsk())
                    .setMid(spotPriceResponseEntity.getMid())
                    .setBid(spotPriceResponseEntity.getBid())
                    .setValue(spotPriceResponseEntity.getValue())
                    .setPerformance(spotPriceResponseEntity.getPerformance())
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException("Not able to fetch data from database");
        }
    }

    @Override
    public SpotPriceByPaginationDTO fetchAssetSpotPrices(PageRequestDTO pageRequest) {
        String fromTime = pageRequest.getFromTime();
        String toTime = pageRequest.getToTime();
        String pageToken = pageRequest.getPageToken();
        String reversePageToken = pageRequest.getReversePageToken();
        int pageSize = pageRequest.getPageSize();

        if (pageSize <= 0) {
            throw new InvalidInputException("Page Size requested should be positive");
        }
        SpotPricesListResponseEntity spotPriceList = SpotPricesListResponseEntity.newBuilder().build();
        List<SpotPriceResponseEntity> allSpotPrices = new ArrayList<>();
        boolean isForward = false;
        boolean isBackward = false;

        if (!pageToken.isEmpty()) {
            isForward = true;
            String decodePageToken = EncodingUtils.decodeBase64(pageToken);
            PageRequestForSpotPriceListEntity spotPricesRequest = PageRequestForSpotPriceListEntity.newBuilder()
                    .setFromTime(decodePageToken)
                    .setToTime(toTime)
                    .setPageSize(pageSize)
                    .build();
            spotPriceList = assetDAO.fetchForwardPaginatedAssetSpotPrices(spotPricesRequest);
            allSpotPrices = spotPriceList.getSpotPricesList();
        } else if (!reversePageToken.isEmpty()) {
            isBackward = true;
            String decodeReversePageToken = EncodingUtils.decodeBase64(reversePageToken);
            PageRequestForSpotPriceListEntity spotPricesRequest = PageRequestForSpotPriceListEntity.newBuilder()
                    .setFromTime(fromTime)
                    .setToTime(decodeReversePageToken)
                    .setPageSize(pageSize)
                    .build();

            spotPriceList = assetDAO.fetchReversePaginatedAssetSpotPrices(spotPricesRequest);
            allSpotPrices = spotPriceList.getSpotPricesList();
        } else if (!fromTime.isEmpty() && !toTime.isEmpty()) {
            PageRequestForSpotPriceListEntity spotPricesRequest = PageRequestForSpotPriceListEntity.newBuilder()
                    .setFromTime(fromTime)
                    .setToTime(toTime)
                    .setPageSize(pageSize)
                    .build();
            spotPriceList = assetDAO.fetchBetweenTimeStampAssetSpotPrices(spotPricesRequest);
            allSpotPrices = spotPriceList.getSpotPricesList();

        }

        SpotPriceByPaginationDTO.Builder result = SpotPriceByPaginationDTO.newBuilder();

        int spotPriceListSize = allSpotPrices.size();

        if (isForward) {
            if (spotPriceListSize > 0) {
                int lastIndex = spotPriceListSize - 1;
                String lastDateInTheList = allSpotPrices.get(lastIndex).getDate();
                pageToken = EncodingUtils.encodeBase64(lastDateInTheList);
                String firstDateInTheList = allSpotPrices.get(0).getDate();
                reversePageToken = EncodingUtils.encodeBase64(firstDateInTheList);
            } else {
                reversePageToken = pageToken;
                pageToken = "No more data to show";
            }
        } else if (isBackward) {
            if (spotPriceListSize > 0) {
                int lastIndex = spotPriceListSize - 1;
                String lastDateInTheList = allSpotPrices.get(lastIndex).getDate();
                pageToken = EncodingUtils.encodeBase64(lastDateInTheList);
                String firstDateInTheList = allSpotPrices.get(0).getDate();
                reversePageToken = EncodingUtils.encodeBase64(firstDateInTheList);
            } else {
                pageToken = reversePageToken;
                reversePageToken = "No previous data to show";
            }
        } else {
            if (spotPriceListSize > 0) {
                int lastIndex = spotPriceListSize - 1;
                String lastDateInTheList = allSpotPrices.get(lastIndex).getDate();
                pageToken = EncodingUtils.encodeBase64(lastDateInTheList);
                String firstDateInTheList = allSpotPrices.get(0).getDate();
                reversePageToken = EncodingUtils.encodeBase64(firstDateInTheList);
            } else {
                pageToken = "No more data to show";
                reversePageToken = "No previous data to show";
            }
        }

        List<SpotPriceResponseDTO> spotPrices = new ArrayList<>();

        allSpotPrices.forEach(spotPrice -> {
            SpotPriceResponseDTO spotPriceFromDb = SpotPriceResponseDTO.newBuilder()
                    .setDate(spotPrice.getDate())
                    .setWeightUnit(spotPrice.getWeightUnit())
                    .setAsk(spotPrice.getAsk())
                    .setBid(spotPrice.getBid())
                    .setMid(spotPrice.getMid())
                    .setValue(spotPrice.getValue())
                    .setPerformance(spotPrice.getPerformance())
                    .build();
            spotPrices.add(spotPriceFromDb);
        });
        result.addAllSpotPrice(spotPrices);
        result.setPageToken(pageToken);
        result.setReversePageToken(reversePageToken);
        return result.build();
    }
}
