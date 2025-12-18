# 우표 이미지 저장 및 DB 관리 로직

이미지 데이터의 효율적인 관리와 앱 성능 유지를 위해 다음과 같은 프로세스를 제안합니다.

## 1. 이미지 저장 흐름 (Save Flow)

1. **CameraX 촬영**: 비트맵 또는 JPEG 데이터를 획득합니다.
2. **이미지 프로세싱 (Stamp Masking)**: Canvas API를 사용하여 다음과 같은 기술적 단계를 거쳐 우표 모양 비트맵을 생성합니다.
    - **Base Bitmap 준비**: 촬영된 사진과 동일한 크기의 빈 `Bitmap`을 생성하고 이를 `Canvas`에 연결합니다.
    - **Path 정의**: 우표 테두리의 톱니바퀴 모양(Perforation)을 `android.graphics.Path`를 이용해 그립니다. (직사각형의 각 변에 반원 형태의 홈을 반복적으로 추가)
    - **Masking**:
        - 먼저 정의한 우표 모양 Path를 Canvas에 그립니다.
        - `Paint` 객체의 `xfermode`를 `PorterDuff.Mode.SRC_IN`으로 설정합니다.
        - 그 위에 원본 사진 비트맵을 그리면, 앞서 그려진 우표 모양 영역 안에만 사진이 남고 나머지는 투명하게 처리됩니다.
    - **Postmark 추가 (선택)**: 완성된 우표 비트맵 위에 날짜 도장(Postmark) 레이어를 한 번 더 합성합니다.
3. **파일 저장 (Internal Storage)**:
    - `context.filesDir` 또는 `context.getExternalFilesDir()` 내의 특정 폴더(`stamps/`)에 파일을 저장합니다.
    - 파일명 예시: `stamp_20231027_1230.png` (마스킹 결과를 유지하기 위해 반드시 PNG 형식으로 저장)
4. **DB 기록 (Room)**:
    - 저장된 파일의 전체 경로(Absolute Path)를 `StampEntity`의 `imagePath` 필드에 저장합니다.

## 2. 이미지 불러오기 흐름 (Load Flow)

1. **DB 쿼리**: ViewModel에서 `StampRepository`를 통해 `StampEntity` 목록을 가져옵니다.
2. **이미지 표시**: Coil 라이브러리를 사용하여 `imagePath` 문자열을 넘겨주면, Coil이 알아서 파일을 읽어 UI(Image 컴포저블)에 렌더링합니다.

## 3. 이 방식의 장점

- **DB 성능 유지**: DB 용량이 작게 유지되어 쿼리가 매우 빠릅니다.
- **메모리 관리**: 안드로이드의 `CursorWindow` 제한(약 2MB)으로부터 자유롭습니다.
- **관리 용이**: 사진 앱 갤러리에 노출되지 않게 '앱 전용 공간'에 숨길 수 있어 데이터 보안에 유리합니다.

## 4. 주의사항

- **파일 삭제**: Room DB에서 데이터를 삭제할 때, 해당 `imagePath`에 있는 실제 물리 파일도 함께 삭제하는 로직을 `Repository`에 포함해야 용량 낭비를 막을 수 있습니다.