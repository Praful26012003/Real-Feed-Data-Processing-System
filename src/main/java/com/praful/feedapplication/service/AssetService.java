package com.praful.feedapplication.service;

import com.praful.feedapplication.protos.PageRequestDTO;
import com.praful.feedapplication.protos.SpotPriceByPaginationDTO;
import com.praful.feedapplication.protos.SpotPriceRequestDTO;
import com.praful.feedapplication.protos.SpotPriceResponseDTO;
import com.praful.feedapplication.protos.SpotPricesRequestEntity;

public interface AssetService {
    int addAssetSpotPrice(SpotPriceRequestDTO spotPriceRequest);

    int addAssetHistoricalSpotPrice(SpotPricesRequestEntity spotPrices);

    SpotPriceResponseDTO fetchAssetSpotPrice();

    SpotPriceByPaginationDTO fetchAssetSpotPrices(PageRequestDTO pageRequest);
}
