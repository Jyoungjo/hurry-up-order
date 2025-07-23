async function requestPayment() {
  try {
    const response = await fetch('http://localhost:9000/payment-service/api/v1/payments/preferred-pg');
    const preferredPG = await response.text();

    if (preferredPG === 'TOSS') {
      requestTossPayment();
    } else if (preferredPG === 'NICE') {
      requestNicePayment();
    } else {
      alert('사용 가능한 결제 수단이 없습니다.');
    }
  } catch (error) {
    console.error('PG 선택 실패:', error);
    alert('결제 수단 조회에 실패했습니다.');
  }
}

const orderId = generateRandomString();
const ordId = 1;

async function requestTossPayment() {
  // ------  SDK 초기화 ------
  // @docs https://docs.tosspayments.com/sdk/v2/js#토스페이먼츠-초기화
  const clientKey = "test_ck_nRQoOaPz8L401kAm5NjN8y47BMw6";
  const customerKey = "ibxjQBr_tKTZ0tOg2K5ep";
  const tossPayments = TossPayments(clientKey);
  // 회원 결제
  // @docs https://docs.tosspayments.com/sdk/v2/js#tosspaymentspayment
  const payment = tossPayments.payment({ customerKey });
  // 비회원 결제
  // const payment = tossPayments.payment({customerKey: TossPayments.ANONYMOUS})
  // ------ '결제하기' 버튼 누르면 결제창 띄우기 ------
  // @docs https://docs.tosspayments.com/sdk/v2/js#paymentrequestpayment
  // 결제를 요청하기 전에 orderId, amount를 서버에 저장하세요.
  // 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도입니다.
  const amount = {
    currency: "KRW",
    value: 10000,
  };

  const response = await fetch('http://localhost:9000/payment-service/api/v1/payments', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ordId,
      orderId,
      amount: amount.value,
    }),
  });

  if (!response.ok) {
    alert("결제 정보 저장 실패");
    return;
  }

  await payment.requestPayment({
    method: "CARD", // 카드 결제
    amount,
    orderId, // 고유 주문번호
    orderName: "토스 티셔츠 외 2건",
    successUrl: window.location.origin + "/payment-service/success.html", // 결제 요청이 성공하면 리다이렉트되는 URL
    failUrl: window.location.origin + "/payment-service/fail.html", // 결제 요청이 실패하면 리다이렉트되는 URL
    customerEmail: "customer123@gmail.com",
    customerName: "김토스",
    customerMobilePhone: "01012341234",
    // 카드 결제에 필요한 정보
    card: {
      useEscrow: false,
      flowMode: "DEFAULT", // 통합결제창 여는 옵션
      useCardPoint: false,
      useAppCardOnly: false,
    },
  });
}

async function requestNicePayment() {
  const response = await fetch('http://localhost:9000/payment-service/api/v1/payments', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ordId,
      orderId,
      amount: 10000,
    }),
  });

  if (!response.ok) {
    alert("결제 정보 저장 실패");
    return;
  }

  await AUTHNICE.requestPay({
    clientId: 'S2_ad644d029d9f4c8c9723bf4dbb16cb77',
    method: 'card',
    orderId,
    amount: 10000,
    goodsName: '나이스페이-상품',
    returnUrl: 'http://localhost:9000/payment-service/nice-return', //API를 호출할 Endpoint 입력
    fnError: function (result) {
      alert('개발자확인용 : ' + result.errorMsg + '')
    }
 });
}

function generateRandomString() {
  return btoa(Math.random()).slice(0, 20);
}