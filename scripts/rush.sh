#!/bin/bash

BASE_URL="http://localhost:8085"
FROM_ACCOUNT=2
TO_ACCOUNT=1
AMOUNT=10000
TOTAL_REQUESTS=50

if [ -z "$TOKEN" ]; then
    echo "Error: TOKEN environment variable is not set"
    exit 1
fi

TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

echo "Starting Rush Test..."
echo "Requests: $TOTAL_REQUESTS"
echo "Each transfer: Rs. 100"
echo "Sender account: $FROM_ACCOUNT"
echo

for i in $(seq 1 $TOTAL_REQUESTS); do
    (
        HTTP_CODE=$(curl -s -o "$TMP_DIR/body_$i" \
            -w "%{http_code}" \
            --connect-timeout 5 \
            --max-time 20 \
            -X POST "$BASE_URL/transfers" \
            -H "Authorization: Bearer $TOKEN" \
            -H "Content-Type: application/json" \
            -d "{\"fromAccountId\":$FROM_ACCOUNT,\"toAccountId\":$TO_ACCOUNT,\"amountPaise\":$AMOUNT}")

        echo "$HTTP_CODE" > "$TMP_DIR/code_$i"
    ) &
done

wait

SUCCESS=$(grep -l '^200$' "$TMP_DIR"/code_* 2>/dev/null | wc -l)
BAD_REQUEST=$(grep -l '^400$' "$TMP_DIR"/code_* 2>/dev/null | wc -l)
OTHER=$(find "$TMP_DIR" -name 'code_*' -type f | wc -l)

OTHER=$((OTHER - SUCCESS - BAD_REQUEST))

echo "========== RUSH TEST RESULT =========="
echo "Total requests : $TOTAL_REQUESTS"
echo "Success (200) : $SUCCESS"
echo "Rejected (400): $BAD_REQUEST"
echo "Other errors  : $OTHER"
echo "===================================="
