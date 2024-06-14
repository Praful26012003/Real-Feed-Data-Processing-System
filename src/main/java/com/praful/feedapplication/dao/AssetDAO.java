package com.praful.feedapplication.dao;

import com.praful.feedapplication.protos.PageRequestForSpotPriceListEntity;
import com.praful.feedapplication.protos.SpotPriceRequestEntity;
import com.praful.feedapplication.protos.SpotPriceResponseEntity;
import com.praful.feedapplication.protos.SpotPricesListResponseEntity;
import com.praful.feedapplication.protos.SpotPricesRequestEntity;

public interface AssetDAO {
    int addAssetSpotPrice(SpotPriceRequestEntity spotPricePriceRequest);

    int addAssetLastDayPrice(SpotPricesRequestEntity spotPrices);

    SpotPriceResponseEntity fetchAssetSpotPrice();

    SpotPricesListResponseEntity fetchBetweenTimeStampAssetSpotPrices(PageRequestForSpotPriceListEntity pageRequestForSpotPriceList);

    SpotPricesListResponseEntity fetchForwardPaginatedAssetSpotPrices(PageRequestForSpotPriceListEntity pageRequestForSpotPriceList);

    SpotPricesListResponseEntity fetchReversePaginatedAssetSpotPrices(PageRequestForSpotPriceListEntity pageRequestForSpotPriceList);
}
