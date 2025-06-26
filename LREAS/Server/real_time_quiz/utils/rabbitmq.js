const amqp = require("amqplib");

// Runs on docker
const RABBITMQ_URL = "amqp://guest:guest@rabbitmq:5672";

// // Runs locally
// const RABBITMQ_URL = "amqp://guest:guest@localhost:5672";


let connection, channel;

  
function sleep(ms) {
return new Promise((resolve) => {
    setTimeout(resolve, ms);
});
}

async function connectRabbitMQ() {
    try {
        console.log("Connecting to ",RABBITMQ_URL );
        await sleep(5000);
        connection = await amqp.connect(RABBITMQ_URL);
        channel = await connection.createChannel();
        console.log("✅ Connected to RabbitMQ");
    } catch (error) {
        console.error("❌ RabbitMQ Connection Error:", error);
    }
}

// Create a queue dynamically for a specific session
async function createQueue(sessionCode) {
    if (!channel) {
        console.error("❌ RabbitMQ channel not initialized.");
        return;
    }
    await channel.assertQueue(sessionCode, { durable: true });
    console.log(`✅ Queue created: ${sessionCode}`);
}

// Push answer to the correct queue
async function pushToQueue({sessionCode, answerData}) {
    if (!channel) {
        console.error("❌ RabbitMQ channel not initialized.");
        return;
    }
    channel.sendToQueue(sessionCode, Buffer.from(JSON.stringify(answerData)));
    console.log(`📤 Answer pushed to queue: ${sessionCode}`);
}

// Pop all answers from a specific queue
async function popAllAnswers(sessionCode) {
    if (!channel) {
        console.error("❌ RabbitMQ channel not initialized.");
        return;
    }

    const messages = [];
    const queueExists = await channel.checkQueue(sessionCode).catch(() => null);
    if (!queueExists) {
        console.error(`❌ Queue does not exist: ${sessionCode}`);
        return messages;
    }

    console.log(`📥 Retrieving answers from queue: ${sessionCode}`);
    let msg;
    while ((msg = await channel.get(sessionCode, { noAck: false }))) {
        messages.push(JSON.parse(msg.content.toString()));
        channel.ack(msg);
    }
    console.log(`✅ Retrieved ${messages.length} answers from queue: ${sessionCode}`);
    return messages;
}

module.exports = { connectRabbitMQ, createQueue, pushToQueue, popAllAnswers };
