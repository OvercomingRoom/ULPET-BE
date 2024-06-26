package com.overcomingroom.ulpet.place.service;

import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.service.WishlistService;
import com.overcomingroom.ulpet.place.domain.Category;
import com.overcomingroom.ulpet.place.domain.dto.PlaceResponseDto;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.domain.entity.PlaceImage;
import com.overcomingroom.ulpet.place.repository.PlaceImageRepository;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceImageRepository placeImageRepository;
    private final WishlistService wishlistService;


    /**
     * 통합 검색을 합니다.
     * 카테고리, 장소 특징, 키워드(장소 명, 주소) 검색이 가능합니다.
     *
     * @param category      카테고리
     * @param feature       특징
     * @param searchKeyword 장소명 또는 주소
     * @return List<PlaceResponseDto>
     */
    public List<PlaceResponseDto> searchPlaces(Category category,
                                               String feature,
                                               String searchKeyword) {
        List<PlaceResponseDto> placeList = placeRepository.search(category, feature, searchKeyword);

        // 이미지 매핑
        for (PlaceResponseDto placeResponseDto : placeList) {
            String imageUrl = getImageUrlByPlaceId(placeResponseDto.getId());
            placeResponseDto.setPlaceImageUrl(imageUrl);
            log.info(placeResponseDto.getPlaceImageUrl());
        }

        return placeList;
    }

    /**
     * placeId를 이욯해 imageUrl정보를 찾아 반환합니다.
     *
     * @param placeId placeId
     * @return imageUrl
     */
    private String getImageUrlByPlaceId(Long placeId) {
        PlaceImage placeImage = placeImageRepository.findByPlaceId(placeId);
        if (placeImage == null || placeImage.getImageUrl() == null || placeImage.getImageUrl().isEmpty()) {
            return null;
        }
        return placeImage.getImageUrl();
    }


    /**
     * placeId를 이욯해 장소 상세를 반환합니다.
     *
     * @param placeId placeId
     * @return PlaceResponseDto
     */
    public PlaceResponseDto getPlaceDetail(Long placeId) {

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        PlaceResponseDto placeResponseDto = PlaceResponseDto.of(place);
        placeResponseDto.setPlaceImageUrl(getImageUrlByPlaceId(placeId));

        return placeResponseDto;
    }


    /**
     * 새로 등록된 장소를 반환합니다.
     *
     * @param numberOfPlaces 보여줄 장소 수
     * @return 새로 등록된 장소 정보 List<PlaceResponseDto>
     */
    public List<PlaceResponseDto> newRegisterPlaces(Long numberOfPlaces) {

        List<PlaceResponseDto> placeList = placeRepository.newRegisterPlaces(numberOfPlaces);

        // 이미지 매핑
        for (PlaceResponseDto placeResponseDto : placeList) {
            String imageUrl = getImageUrlByPlaceId(placeResponseDto.getId());
            placeResponseDto.setPlaceImageUrl(imageUrl);
            log.info(placeResponseDto.getPlaceImageUrl());
        }

        return placeList;
    }

    /**
     * 장소를 삭제합니다.
     */
    @Transactional
    public void deletePlace(Long placeId) {

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        // 위시리스트에서 장소 삭제
        wishlistService.PreRemovePlaceFromWishList(place);

        // 장소 삭제
        placeRepository.delete(place);
    }
}
