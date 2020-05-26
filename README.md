# Rest API 기반 결제시스템
결제 / 취소 요청을 받아서 String 데이터로 카드사와 통신하는 서비스 개발.

## 개발 프레임워크
* Spring boot 2.2.5.RELEASE
* Java 1.8
* Junit 5
* Spring JPA
* H2

## 테이블 설계

![Image](db_diagram.png?raw=true)

<br>

#### PAYMENT 
* 결제, 취소 거래내역을 저장하는 테이블

| 컬럼 | 설명 | 
| :---: |     :---:     |
| tid   | 거래 번호    | 
| type    | 결제/취소를 구분 예) PAYMENT,CANCEL      |  
| amount    | 거래 금액      |  
| vat    | 부가가치세      |  
| status    |  거래 상태  예) DN, FL, WT     |  
| created_at    |  최소 거래 요청 시     |  
| updated_at    |  완료 시간 |  

<br>

#### CARD 
* 카드 정보 (카드번호, 유효날짜, cvc)의 hash 값과 암호화한 데이터를 저장.

| 컬럼 | 설명 | 
| :---: |     :---:     |
| hash_key   | 카드 정보의 hash     | 
| info*    | 카드 정를 암호화한 데이터    |
  
*info의 데이터 카드번호,유효날짜,cvc는 '|'으로 구분.

<br>

#### PAYMENT_CARD
* 거래내역과 카드 정보를 분리하기 위해서 생성한 브릿지 테이블
* 취소는 결제한 카드를 사용하므로 최초 결제시에만 저장

| 컬럼 | 설명 | 
| :---: |     :---:     |
| payment_id   | payment 테이블의 id    | 
| card_id    | card 테이블의 id    |

<br>

#### PAYMENT_CARD_DETAIL 
* 결제 시마다 변하는 데이터 (할부 개월)과 외부 업체(카드사)의 거래 정보를 저장하는 테이블

| 컬럼 | 설명 | 
| :---: |     :---:     |
| tid   | 거래 번호    | 
| card_tid    | 카드사에서 생성한 거래 번호      |  
| installment    | 할부/일시불 구분      |  
| approved_at    | 카드사의 거래 날짜, 시간      |  

<br>

#### CARD_COMPANY_TRANSACTION

| 컬럼 | 설명 | 
| :---: |     :---:     |
| id   | 거래 번호    | 
| request    | 통신 메시지 (결제/취소에 필요한 정보를 String 메시지로 저장한 데이터) |  

<br>
<br>


## 문제해결 전략
### 카드사로 전송하는 String 데이터
요구사항:
* 카드사에 전송하는 450자의 스트링은 숫자나 문자 데이터를 포함하고 있으며 데이터의 형태에 따라서 좌우에 공백이나 숫자를 자릿수에 맞춰서 추가한다  
 예) 숫자 (3 -> "___ 3"), (3 -> "0003"), (3 -> "3___"), 문자 (HOMEWORK -> "HOMEWORK__")
* 데이터는 450자의 스트링에서 지정된 위치에 놓여야 한다.    

해결방법: \
스트링을 구성하는 데이터에 대응하는 모델을 만들고 각각의 필드에 애너테이션을 추가하여 파싱 함. 

예) 숫자, 우측정렬, 자릿수에 맞춰서 좌측에 빈칸을 추가한다면,   
```
@Order(6) @DigitField(length=4, padding=Padding.LEFT_EMPTY) 
private long amount;
```

예) 문자, 좌측정렬, 자릿수에 맞춰서 우측에 빈칸을 추가한다면, 
```
@Order(1) @TextField(length = 20) 
private String tid;
```

<br>

### 중복 요청 방지
같은 시간, 같은 조건의 결제/취소 요청을 방지합니다. \
요구사항: 
* 결제 : 하나의 카드번호로 동시에 결제를 불가. 
* 전체취소 & 부분취소 : 결제 한 건에 대한 전체 혹은 부분 취소를 동시에 할 수 없습니다. 

해결방법: \
* 카드나 금액이 같은 경우, 2초 정도의 요청 간격이 있다면 각각의 요청이라고 판단 할 수 있으므로 expiryMap을 사용. 
* 같은 시간의 결제나 취소 요청이라면 중복 여부를 카드(번호, cvc, 유효기간), 금액 등 다양한 조건으로 판단할 수 있으므로 키 값을 지정할 수 있는 애노테이션을 추가 했습니다. 
* @DisallowConcurrentRequest 와 @HashingTargt 조합으로 결제/취소를 처리하는 메서드에 사용합니다. 
아래의 예제에는 카드 번호와 금액을 대상으로 중복 여부를 체크하도록 했지만 유효기간, cvc에 @HashingTarget 추가로 중복 조건을 보다 엄격하게 변경할 수 있습니다. 

```
//적용 예
@DisallowConcurrentRequest
public Payment approve(@HashingTarget final String cardNumber, String validDate, final String cvc, String installment, @HashingTarget long payAmount, Long vat) {
    // do Something    
}
```    
 
<br>
 

## 빌드 실행 방법 
```
mvn spring-boot:run
```



