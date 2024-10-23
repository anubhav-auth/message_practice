package com.anubhav_auth.message_practice.di


import android.app.Application
import androidx.room.Room
import com.anubhav_auth.message_practice.data.local.MessagesBackLogDAO
import com.anubhav_auth.message_practice.data.local.MessagesDAO
import com.anubhav_auth.message_practice.data.local.MessagesDatabase
import com.anubhav_auth.message_practice.data.local.MessagesStatusUpdateBackLogDAO
import com.anubhav_auth.message_practice.data.local.TopicsSubscribedDAO
import com.anubhav_auth.message_practice.data.remote.ApolloMessageClient
import com.anubhav_auth.message_practice.data.repository.MessageRepository
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.apollographql.apollo.network.ws.GraphQLWsProtocol
import com.apollographql.apollo.network.ws.WebSocketNetworkTransport
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApolloClient():ApolloClient{
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        // Create OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging) // Add logging interceptor
            .build()

        val apolloClient = ApolloClient.Builder()
            .serverUrl("http://10.190.41.37:8080/graphql")
            .subscriptionNetworkTransport(
                WebSocketNetworkTransport.Builder()
                    .protocol(GraphQLWsProtocol.Factory())
                    .serverUrl("http://10.190.41.37:8080/graphql")
                    .build()
            )
            .build()

        return apolloClient
    }
    @Provides
    @Singleton
    fun provideMessageClient(apolloClient: ApolloClient): ApolloMessageClient {
        return ApolloMessageClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideMessagesDataBase(app: Application): MessagesDatabase {
        return Room.databaseBuilder(
            app,
            MessagesDatabase::class.java,
            "messages.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMessagesDao(messagesDatabase: MessagesDatabase): MessagesDAO {
        return messagesDatabase.messagesDao()
    }

    @Provides
    @Singleton
    fun provideMessagesBackLogDao(messagesDatabase: MessagesDatabase): MessagesBackLogDAO {
        return messagesDatabase.messagesBackLogDao()
    }

    @Provides
    @Singleton
    fun providesTopicsSubscribedDao(messagesDatabase: MessagesDatabase): TopicsSubscribedDAO {
        return messagesDatabase.topicsSubscribedDao()
    }

    @Provides
    @Singleton
    fun providesMessagesStatusUpdateBackLogDao(messagesDatabase: MessagesDatabase): MessagesStatusUpdateBackLogDAO {
        return messagesDatabase.messagesStatusUpdateBackLogDao()
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        apolloMessageClient: ApolloMessageClient,
        messagesDAO: MessagesDAO,
        messagesBackLogDAO: MessagesBackLogDAO,
        messagesStatusUpdateBackLogDAO: MessagesStatusUpdateBackLogDAO,
        topicsSubscribedDAO: TopicsSubscribedDAO
    ): MessageRepository {
        return MessageRepository(apolloMessageClient, messagesDAO,messagesBackLogDAO, messagesStatusUpdateBackLogDAO, topicsSubscribedDAO)
    }

}