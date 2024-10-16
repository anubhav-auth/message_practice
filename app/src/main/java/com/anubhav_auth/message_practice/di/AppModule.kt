package com.anubhav_auth.message_practice.di


import com.anubhav_auth.message_practice.ApolloMessageClient
import com.anubhav_auth.message_practice.MessageClient
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
            .serverUrl("http://10.190.36.131:8080/graphql")
            .subscriptionNetworkTransport(
                WebSocketNetworkTransport.Builder()
                    .protocol(GraphQLWsProtocol.Factory())
                    .serverUrl("http://10.190.36.131:8080/graphql")
                    .build()
            )
            .build()

        return apolloClient
    }

    @Provides
    @Singleton
    fun provideMessageClient(apolloClient: ApolloClient): ApolloMessageClient{
        return ApolloMessageClient(apolloClient)
    }
}