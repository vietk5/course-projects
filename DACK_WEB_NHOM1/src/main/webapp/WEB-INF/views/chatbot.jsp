<style>
  df-messenger {
    --df-messenger-bot-message: #e3f2fd;
    --df-messenger-button-titlebar-color: #1976d2;
    --df-messenger-chat-background-color: #ffffff;
    --df-messenger-font-color: #000000;
    --df-messenger-send-icon: #1976d2;

    --df-messenger-chat-window-height: 100px; /* máº·c Äá»nh ~600px */


    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 9999;
    transform: scale(0.91);
    transform-origin: bottom right;
  }
        .chat-buttons {
            position: fixed;
            bottom: 100px; /* Đã thay đổi */
            right: 39px;
            display: flex;
            flex-direction: column;
            gap: 10px;
            z-index: 9999;
        }

        .chat-button {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            transition: transform 0.3s ease-in-out;
        }

        .chat-button:hover {
            transform: scale(1.1);
        }

        .messenger-button {
            background-color: #007bff; /* Màu xanh Messenger */
            color: white;
            font-size: 24px;
        }

        .zalo-button {
            background-color: #008cd7; /* Màu xanh Zalo */
        }

        .zalo-button img {
            width: 30px;
            height: 30px;
        }
        .messenger-button img {
            width: 30px;
            height: 30px;
        }
</style>
<script src="https://www.gstatic.com/dialogflow-console/fast/messenger/bootstrap.js?v=1"></script>
    <df-messenger
      chat-icon="data:image/jpeg;base64,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"
      chat-title="ElectroMart Chatbot"
      agent-id="7ee21fcb-898b-40f5-bcb8-44035e5429d1"
      language-code="vi"
    ></df-messenger>

    <div class="chat-buttons">
        <a href="https://www.facebook.com/ec.duy.9/" target="_blank" class="chat-button messenger-button">
            <!--<i class="fab fa-facebook-messenger"></i>-->
            <img src="${pageContext.request.contextPath}/assets/img/messenger-logo.png" alt="Zalo Chat">
        </a>
        <a href="https://zalo.me/0329455541" target="_blank" class="chat-button zalo-button">
            <img src="${pageContext.request.contextPath}/assets/img/logo-zalo.png" alt="Zalo Chat">
            <!--<img src="https://hienlaptop.com/wp-content/uploads/2024/12/logo-zalo-vector-7.jpg" alt="Zalo Chat">-->
        </a>
    </div>
